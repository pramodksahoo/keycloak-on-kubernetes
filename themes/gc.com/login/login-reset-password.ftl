<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true displayMessage=!messagesPerField.existsError('username'); section>
<div class="rows">
<div class="column-12" id="copy_full">
<div class="rows">
          
    <#if section = "form">
        <h1 class="center-column column-5 mvxl">${msg("emailForgotTitle")}</h1>
        <div class="center-column column-5 mvxl border-gray-as mln">
        <div id="account_prompt_section"><p class="mvl text-center px-2">${msg("emailInstruction")}</p></div>
        <form id="kc-reset-password-form" class="${properties.kcFormClass!} paxl" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!} ">
                    <label for="username" class="${properties.kcLabelClass!}"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="email" id="username" name="username" required="required" class="${properties.kcInputClass!}" autofocus value="${(auth.attemptedUsername!'')}" aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
                    
                </div>
            </div>


            <div class="${properties.kcFormGroupClass!}">
                <#if messagesPerField.existsError('username')>
                        <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('username'))?no_esc}
                        </span>
                    </#if>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="alert alert-info" id="pw-desc">
                        ${msg("passwordRequirements")}
                    </div>
            </div>

            

            <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmitResetPassword")}"/>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                        <span><a href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
                    </div>
                </div>
            </div>

        </form>
    </#if>
    </div></div>
    </div></div>
</@layout.registrationLayout>
