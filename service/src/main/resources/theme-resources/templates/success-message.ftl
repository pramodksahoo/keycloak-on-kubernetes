<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=true; section>
    <#if section == "header">
        ${msg("successTitle", "Success")}
    <#elseif section == "form">
        <p>${msg("successMessage", "Your registration is complete. You will be redirected shortly.")}</p>
        <script type="text/javascript">
            // Redirect after 3 seconds (3000 milliseconds)
            setTimeout(function() {
                window.location.href = "${redirectUrl}";
            }, 3000);
        </script>
    </#if>
</@layout.registrationLayout>
