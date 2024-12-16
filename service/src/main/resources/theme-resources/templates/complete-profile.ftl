<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('first_name','last_name', 'password'); section>
    <#if section = "header">
        ${msg("completeProfileTitle")}
    <#elseif section = "form">
        <h2>${msg("userEmail",(email!''))}</h2>
        <p>${msg("completeProfileText")}</p>
        <form id="kc-info-update-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="first_name" class="${properties.kcLabelClass!}">${msg("firstNameLabel")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="first_name" name="first_name" class="${properties.kcInputClass!}"
                           value="${first_name!}" required aria-invalid="<#if messagesPerField.existsError('first_name')>true</#if>"/>
                    <#if messagesPerField.existsError('first_name')>
                        <span id="input-error-first-name" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('first_name'))?no_esc}
								</span>
                    </#if>
                </div>
            </div>


            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="last_name" class="${properties.kcLabelClass!}">${msg("lastNameLabel")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="last_name" name="last_name" class="${properties.kcInputClass!}"
                           value="${last_name!}" required aria-invalid="<#if messagesPerField.existsError('last_name')>true</#if>"/>
                    <#if messagesPerField.existsError('last_name')>
                        <span id="input-error-last-name" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('last_name'))?no_esc}
								</span>
                    </#if>
                </div>
            </div>


            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="password" class="${properties.kcLabelClass!}">${msg("passwordLabel")}</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="password" id="password" name="password" class="${properties.kcInputClass!}"
                           value="${password!}" required aria-invalid="<#if messagesPerField.existsError('password')>true</#if>"/>
                    <#if messagesPerField.existsError('password')>
                        <span id="input-error-password" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
										${kcSanitize(messagesPerField.get('password'))?no_esc}
								</span>
                    </#if>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doSubmit")}"/>
                </div>
            </div>
        </form>
    </#if>
</@layout.registrationLayout>
