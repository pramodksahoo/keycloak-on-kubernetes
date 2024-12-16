<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('totp'); section>
    <#if section="header">
        ${msg("doLogIn")}
    <#elseif section="form">
        <div class="rows">
          <div class="column-12" id="copy_full">
            <div class="rows">
            <h1 class="center-column column-5 mvxl">Login Verification</h1>
            <div class="center-column column-5 mvxl border-gray-as mln">
            <div id="account_prompt_section"><p class="mvl">Please enter your email address and the code you received there.</p></div>
        <form action="${url.loginAction}" class="${properties.kcFormClass!} paxl" id="kc-u2f-login-form" method="post">

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="verificationCode" class="${properties.kcLabelClass!}">Email Address</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input id="email" name="email" required="required" class="${properties.kcInputClass!}" type="email"/>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="verificationCode" class="${properties.kcLabelClass!}">Verification Code</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input id="code" name="code" required="required" class="${properties.kcInputClass!}" type="text" inputmode="numeric" pattern="[0-9A-Za-z]*"/>
                </div>
            </div>


            <div class="${properties.kcFormGroupClass!}">
                <div class="alert alert-info" id="pw-desc">
                        The code is exactly 6 alphanumeric characters long.
                    </div>
            </div>

            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                   type="submit" value="${msg("do2FALogIn")}"/>

        </form>
        </div>
        </div>
        </div>
        </div>
    </#if>
</@layout.registrationLayout>