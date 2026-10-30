<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />
<jsp:include page="/WEB-INF/views/common/alerts.jsp" />

<div class="container" style="margin-top: 36px; margin-bottom: 60px;">
    <div style="max-width: 920px; margin: 0 auto;">
        <div class="section-header">
            <div>
                <span class="hero-tagline">Technical Architecture</span>
                <h1 class="section-title">About CulinaryCraft System</h1>
                <p class="section-subtitle">A modern Java MVC web application powered by XML DOM persistence</p>
            </div>
        </div>

        <div class="detail-card" style="margin-bottom: 28px;">
            <h2 class="detail-card-title">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="16" x2="12" y2="12"/>
                    <line x1="12" y1="8" x2="12.01" y2="8"/>
                </svg>
                <span>Overview &amp; Design Philosophy</span>
            </h2>
            <p style="margin-bottom: 16px;">
                <strong>CulinaryCraft</strong> is an enterprise-grade, clean-architecture web application designed for simple, reliable, and aesthetically pleasing recipe management. Built strictly following the <strong>Model-View-Controller (MVC)</strong> pattern, it separates business data models, XML repository operations, servlet request controllers, and dynamic JSP view presentation.
            </p>
            <p>
                Unlike basic educational demos, this system features robust multi-field validation, full CRUD operations, live search filtering, responsive dynamic serving calculations, resilient external XML persistence that survives application restarts, and hardened XXE security protection.
            </p>
        </div>

        <!-- Tech Stack Badges -->
        <div class="detail-card" style="margin-bottom: 28px;">
            <h2 class="detail-card-title">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polygon points="12 2 2 7 12 12 22 7 12 2"/>
                    <polyline points="2 17 12 22 22 17"/>
                    <polyline points="2 12 12 17 22 12"/>
                </svg>
                <span>Technology Stack</span>
            </h2>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-top: 16px;">
                <div style="background: var(--bg-surface-alt); padding: 16px; border-radius: var(--radius-md);">
                    <strong>Backend Core</strong>
                    <div style="color: var(--text-secondary); font-size: 0.9rem; margin-top: 4px;">Java 17, Jakarta Servlet API 6.0</div>
                </div>
                <div style="background: var(--bg-surface-alt); padding: 16px; border-radius: var(--radius-md);">
                    <strong>Presentation Layer</strong>
                    <div style="color: var(--text-secondary); font-size: 0.9rem; margin-top: 4px;">JSP, JSTL 3.0, HTML5, CSS3, JS</div>
                </div>
                <div style="background: var(--bg-surface-alt); padding: 16px; border-radius: var(--radius-md);">
                    <strong>Data Engine</strong>
                    <div style="color: var(--text-secondary); font-size: 0.9rem; margin-top: 4px;">W3C DOM XML Parser &amp; Transformer</div>
                </div>
                <div style="background: var(--bg-surface-alt); padding: 16px; border-radius: var(--radius-md);">
                    <strong>Application Server</strong>
                    <div style="color: var(--text-secondary); font-size: 0.9rem; margin-top: 4px;">Apache Tomcat 10.1+ (Embedded &amp; Standalone)</div>
                </div>
            </div>
        </div>

        <!-- XML Persistence Details -->
        <div class="detail-card" style="margin-bottom: 28px;">
            <h2 class="detail-card-title">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                    <polyline points="14 2 14 8 20 8"/>
                    <line x1="16" y1="13" x2="8" y2="13"/>
                    <line x1="16" y1="17" x2="8" y2="17"/>
                    <polyline points="10 9 9 9 8 9"/>
                </svg>
                <span>XML Storage Engine &amp; Path</span>
            </h2>
            <p style="margin-bottom: 12px;">
                Recipe data is maintained externally so that modifications are preserved during redeployments. Currently active XML storage location:
            </p>
            <div style="background: var(--bg-surface-alt); padding: 14px 18px; border-radius: var(--radius-sm); font-family: monospace; font-size: 0.9rem; word-break: break-all; margin-bottom: 16px; border: 1px solid var(--border-light);">
                ${xmlPath}
            </div>

            <h3 style="font-size: 1.05rem; margin-bottom: 8px;">XML Structure Sample:</h3>
            <pre style="background: #2B2D42; color: #F8F9FA; padding: 16px; border-radius: var(--radius-md); font-size: 0.85rem; overflow-x: auto; line-height: 1.5;">&lt;recipes&gt;
    &lt;recipe id="1"&gt;
        &lt;name&gt;Vegetable Pasta&lt;/name&gt;
        &lt;category&gt;Dinner&lt;/category&gt;
        &lt;description&gt;A delicious homemade vegetable pasta.&lt;/description&gt;
        &lt;ingredients&gt;
            &lt;ingredient&gt;Pasta&lt;/ingredient&gt;
            &lt;ingredient&gt;Tomatoes&lt;/ingredient&gt;
        &lt;/ingredients&gt;
        &lt;instructions&gt;
            &lt;step&gt;Boil the pasta.&lt;/step&gt;
            &lt;step&gt;Mix and serve.&lt;/step&gt;
        &lt;/instructions&gt;
        &lt;preparationTime&gt;15&lt;/preparationTime&gt;
        &lt;cookingTime&gt;20&lt;/cookingTime&gt;
        &lt;servings&gt;2&lt;/servings&gt;
        &lt;difficulty&gt;Easy&lt;/difficulty&gt;
        &lt;imageUrl&gt;https://...&lt;/imageUrl&gt;
    &lt;/recipe&gt;
&lt;/recipes&gt;</pre>
        </div>

        <!-- Security Features -->
        <div class="detail-card">
            <h2 class="detail-card-title">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect width="18" height="11" x="3" y="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                </svg>
                <span>Security Protections</span>
            </h2>
            <ul style="margin-left: 20px; line-height: 1.8;">
                <li><strong>XXE (XML External Entity) Defense:</strong> <code>disallow-doctype-decl</code>, disabling external general and parameter entities, and restricting DTD access.</li>
                <li><strong>Thread Safety:</strong> ReentrantReadWriteLock controls concurrent modifications to guarantee file consistency.</li>
                <li><strong>Safe Form Handling:</strong> Post/Redirect/Get pattern prevents duplicate form resubmissions on browser refresh.</li>
                <li><strong>Input Sanitization:</strong> Strict length and format validation on all model attributes.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
