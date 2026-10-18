package com.orangehrm.automation.listeners;

import com.orangehrm.automation.base.BaseTest;
import com.orangehrm.automation.reporting.TestSteps;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

/** Captures test steps and failures in the project's interactive HTML report. */
public class TestListener implements ITestListener, ISuiteListener {
    private static final Path REPORT_PATH = findProjectRoot()
            .resolve(Path.of("target", "test-reports", "OrangeHRM-Test-Report.html"));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final List<TestRecord> RESULTS = new ArrayList<>();
    private static final Set<ITestResult> RECORDED_RESULTS = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public synchronized void onStart(ISuite suite) {
        RESULTS.clear();
        RECORDED_RESULTS.clear();
    }

    @Override
    public synchronized void onFinish(ISuite suite) {
        writeReport();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        addResult(result, "PASS", "", "");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (hasRecorded(result)) return;
        Object instance = result.getInstance();
        String screenshot = instance instanceof BaseTest test
                ? captureScreenshot(result, test.getDriver()) : "";
        Throwable failure = result.getThrowable();
        addResult(result, "FAIL", failure == null ? "Test failed" : stackTrace(failure), screenshot);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Throwable failure = result.getThrowable();
        addResult(result, "SKIP", failure == null ? "Test was skipped" : stackTrace(failure), "");
    }

    /** Called from BaseTest teardown so VS Code's direct TestNG runner also updates this report. */
    public static void recordFromTestTeardown(ITestResult result, WebDriver driver) {
        String status = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASS";
            case ITestResult.FAILURE -> "FAIL";
            default -> "SKIP";
        };
        String screenshot = status.equals("FAIL") ? captureScreenshot(result, driver) : "";
        Throwable failure = result.getThrowable();
        String details = failure == null ? "" : stackTrace(failure);
        addResult(result, status, details, screenshot);
    }

    private static synchronized void addResult(ITestResult result, String status, String details, String screenshot) {
        if (!RECORDED_RESULTS.add(result)) return;
        String name = result.getMethod().getMethodName();
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) name += " " + Arrays.toString(parameters);
        RESULTS.add(new TestRecord(name, result.getTestClass().getName(), status,
                Math.max(0, result.getEndMillis() - result.getStartMillis()), details, screenshot,
                TestSteps.snapshot()));
        writeReport();
    }

    private static boolean hasRecorded(ITestResult result) {
        synchronized (TestListener.class) { return RECORDED_RESULTS.contains(result); }
    }

    private static String captureScreenshot(ITestResult result, WebDriver driver) {
        if (driver == null) return "";
        try {
            Path directory = findProjectRoot().resolve(Path.of("target", "screenshots"));
            Files.createDirectories(directory);
            String safeName = result.getMethod().getMethodName().replaceAll("[^A-Za-z0-9._-]", "_");
            String name = safeName + "-" + System.currentTimeMillis() + ".png";
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), directory.resolve(name), StandardCopyOption.REPLACE_EXISTING);
            return "../screenshots/" + name;
        } catch (Exception exception) {
            result.setAttribute("screenshot-error", exception.getMessage());
            return "";
        }
    }

    private static synchronized void writeReport() {
        try {
            long passed = RESULTS.stream().filter(item -> item.status.equals("PASS")).count();
            long failed = RESULTS.stream().filter(item -> item.status.equals("FAIL")).count();
            long skipped = RESULTS.stream().filter(item -> item.status.equals("SKIP")).count();
            StringBuilder html = new StringBuilder("""
                    <!doctype html>
                    <html lang="en"><head><meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>OrangeHRM Test Execution Report</title>
                    <style>
                    :root{font-family:Inter,"Segoe UI",Arial,sans-serif;color:#152238;background:#f3f6fb}
                    *{box-sizing:border-box}body{margin:0;padding:34px 20px}main{max-width:1040px;margin:auto}
                    .hero{padding:26px 30px;color:white;border-radius:18px;background:linear-gradient(120deg,#102f52,#176b87);box-shadow:0 12px 30px #16324f24}
                    .eyebrow{text-transform:uppercase;letter-spacing:.14em;font-size:12px;opacity:.75}.hero h1{margin:8px 0;font-size:30px}
                    .hero p{margin:0;color:#d9eaf4}.generated{font-size:13px;margin-top:18px;color:#d9eaf4}
                    .summary{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:13px;margin:20px 0}
                    .metric{background:#fff;border:1px solid #e3eaf2;border-radius:13px;padding:17px 19px;box-shadow:0 3px 12px #17324d0b}
                    .metric span{display:block;color:#62758a;font-size:13px}.metric strong{display:block;font-size:27px;margin-top:4px}
                    .metric.pass strong{color:#16824b}.metric.fail strong{color:#c43d4d}.metric.skip strong{color:#a36d09}
                    .toolbar{display:flex;gap:10px;flex-wrap:wrap;margin:22px 0 12px}.toolbar input,.toolbar select{font:inherit;background:#fff;border:1px solid #ccd7e3;border-radius:9px;padding:10px 12px;color:#22354a}
                    .toolbar input{flex:1;min-width:220px}.section-title{font-size:17px;margin:20px 0 12px}
                    .test-card{background:#fff;border:1px solid #e1e8f0;border-radius:13px;margin:10px 0;overflow:hidden;box-shadow:0 3px 12px #17324d08}
                    .test-head{display:flex;align-items:center;gap:12px;padding:16px 18px}.case-link{font-weight:650;color:#145f87;text-decoration:none;flex:1}
                    .case-link:hover{text-decoration:underline}.chevron{display:inline-block;color:#718398;margin-right:9px;transition:transform .15s}
                    .case-link[aria-expanded="true"] .chevron{transform:rotate(90deg)}.badge{font-size:11px;font-weight:750;letter-spacing:.04em;border-radius:999px;padding:5px 9px}
                    .badge.pass{color:#116b3d;background:#e4f6ec}.badge.fail{color:#a92436;background:#fde9ec}.badge.skip{color:#805705;background:#fff3d6}
                    .duration{font-size:12px;color:#62758a;white-space:nowrap}.test-meta{padding:0 18px 14px;color:#74869a;font-size:12px}
                    .steps-panel{display:none;border-top:1px solid #e5ebf2;background:#f9fbfd;padding:15px 20px 18px}.steps-panel.open{display:block}
                    .steps-title{font-size:12px;font-weight:750;text-transform:uppercase;letter-spacing:.08em;color:#62758a}
                    ol.steps{padding-left:22px;margin:10px 0 0}ol.steps li{padding:5px 0 5px 4px;color:#2a3c50;line-height:1.45}
                    .failure{margin-top:14px}.failure summary{cursor:pointer;color:#a92436;font-weight:650}.failure pre{white-space:pre-wrap;overflow-wrap:anywhere;background:#fff3f4;padding:12px;border-radius:8px;font-size:12px}
                    .screenshot{display:inline-block;margin-top:12px;color:#145f87}.empty{background:#fff;border:1px solid #e1e8f0;border-radius:13px;padding:24px;color:#62758a}
                    @media(max-width:600px){body{padding:16px 12px}.summary{grid-template-columns:repeat(2,minmax(0,1fr))}.hero{padding:22px}.hero h1{font-size:24px}.duration{display:none}}
                    </style></head><body><main>
                    <header class="hero"><div class="eyebrow">UI automation · Selenium + TestNG</div>
                    <h1>OrangeHRM Test Report</h1><p>Execution results and expandable test steps</p>
                    """);
            html.append("<div class=\"generated\">Last updated: ")
                    .append(escape(LocalDateTime.now().format(DATE_FORMAT))).append("</div></header>");
            html.append("<section class=\"summary\"><div class=\"metric\"><span>Total tests</span><strong>")
                    .append(RESULTS.size()).append("</strong></div><div class=\"metric pass\"><span>Passed</span><strong>")
                    .append(passed).append("</strong></div><div class=\"metric fail\"><span>Failed</span><strong>")
                    .append(failed).append("</strong></div><div class=\"metric skip\"><span>Skipped</span><strong>")
                    .append(skipped).append("</strong></div></section>");
            html.append("<div class=\"toolbar\"><input id=\"search\" type=\"search\" placeholder=\"Find a test case...\" oninput=\"filterTests()\">")
                    .append("<select id=\"status-filter\" onchange=\"filterTests()\"><option value=\"all\">All statuses</option>")
                    .append("<option value=\"pass\">Passed</option><option value=\"fail\">Failed</option><option value=\"skip\">Skipped</option></select></div>")
                    .append("<h2 class=\"section-title\">Test cases <span style=\"color:#718398;font-weight:400\">(select a name to view its steps)</span></h2>");

            if (RESULTS.isEmpty()) {
                html.append("<p class=\"empty\">No test results have been recorded yet.</p>");
            } else {
                for (int index = 0; index < RESULTS.size(); index++) {
                    TestRecord item = RESULTS.get(index);
                    String panelId = "steps-" + index;
                    String statusClass = item.status.toLowerCase();
                    html.append("<article class=\"test-card\" data-status=\"").append(statusClass)
                            .append("\" data-name=\"").append(escape(item.name.toLowerCase())).append("\"><div class=\"test-head\">")
                            .append("<a class=\"case-link\" href=\"#").append(panelId)
                            .append("\" aria-expanded=\"false\" onclick=\"toggleSteps(event,this,'").append(panelId).append("')\">")
                            .append("<span class=\"chevron\">&#9656;</span>").append(escape(item.name)).append("</a>")
                            .append("<span class=\"badge ").append(statusClass).append("\">").append(item.status)
                            .append("</span><span class=\"duration\">").append(item.durationMillis).append(" ms</span></div>")
                            .append("<div class=\"test-meta\">").append(escape(item.className)).append("</div>")
                            .append("<section class=\"steps-panel\" id=\"").append(panelId).append("\"><div class=\"steps-title\">Execution steps</div><ol class=\"steps\">");
                    if (item.steps.isEmpty()) html.append("<li>No detailed steps were recorded for this test.</li>");
                    else for (String step : item.steps) html.append("<li>").append(escape(step)).append("</li>");
                    html.append("</ol>");
                    if (!item.screenshot.isBlank()) html.append("<a class=\"screenshot\" href=\"")
                            .append(escape(item.screenshot)).append("\" target=\"_blank\">Open failure screenshot</a>");
                    if (!item.details.isBlank()) html.append("<details class=\"failure\"><summary>Failure details</summary><pre>")
                            .append(escape(item.details)).append("</pre></details>");
                    html.append("</section></article>");
                }
            }
            html.append("""
                    <script>
                    function toggleSteps(event,link,id){event.preventDefault();const panel=document.getElementById(id);const open=panel.classList.toggle('open');link.setAttribute('aria-expanded',open);}
                    function filterTests(){const query=document.getElementById('search').value.toLowerCase();const status=document.getElementById('status-filter').value;document.querySelectorAll('.test-card').forEach(card=>{card.hidden=!(card.dataset.name.includes(query)&&(status==='all'||card.dataset.status===status));});}
                    </script></main></body></html>
                    """);

            Files.createDirectories(REPORT_PATH.getParent());
            Files.writeString(REPORT_PATH, html.toString(), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            System.err.println("Could not write HTML test report: " + exception.getMessage());
        }
    }

    private static Path findProjectRoot() {
        Path directory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        for (Path current = directory; current != null; current = current.getParent()) {
            if (Files.isRegularFile(current.resolve("pom.xml"))) return current;
        }
        return directory;
    }

    private static String stackTrace(Throwable throwable) {
        java.io.StringWriter buffer = new java.io.StringWriter();
        throwable.printStackTrace(new java.io.PrintWriter(buffer));
        return buffer.toString();
    }

    private static String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private record TestRecord(String name, String className, String status, long durationMillis,
                              String details, String screenshot, List<String> steps) { }
}
