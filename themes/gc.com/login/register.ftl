<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "header">
        
    <#elseif section = "form">
    <div class="rows">
          <div class="column-12" id="copy_full">
            <div class="rows">
              <h1 class="center-column column-6 mvxl">${msg("registerTitle")}</h1>
              <div class="center-column column-6 mvxl border-gray-as mln">
                <ul id="login-nav" class="nav-base nav-tab two">
                  <li class="two">
                    <a href="${url.loginUrl}">${msg("loginAccountTitle")}</a>
                  </li>
                  <li class="active two link-right">
                    <a href="${url.registrationUrl}">Register</a>
                  </li>
                </ul>
                <div class="sign-up-banner">
                    <h3 class="sign-up-banner_title">Benefits of registration</h3>
                    <ul class="sign-up-banner_list">
                        <li class="sign-up-banner_list-item">
                            <img class="sign-up-banner-icon" width="height=" src="https://gccdn.giftcards.com/icons/star-icon.svg" alt="star icon">
                            <span class="sign-up-banner-text">Check out quicker with stored payments</span>
                        </li>
                        <li class="sign-up-banner_list-item">
                            <img class="sign-up-banner-icon" width="height=" src="https://gccdn.giftcards.com/icons/star-icon.svg" alt="star icon">
                            <span class="sign-up-banner-text">View order status and history</span>
                        </li>
                        <li class="sign-up-banner_list-item">
                            <img class="sign-up-banner-icon" width="height=" src="https://gccdn.giftcards.com/icons/star-icon.svg" alt="star icon">
                            <span class="sign-up-banner-text">Opt-in for early access and promotions</span>
                        </li>
                        <li class="sign-up-banner_list-item">
                            <img class="sign-up-banner-icon" width="height=" src="https://gccdn.giftcards.com/icons/star-icon.svg" alt="star icon">
                            <span class="sign-up-banner-text">Enroll for G-Money reward points</span>
                        </li>
                    </ul>
                </div>

        <form id="kc-register-form" class="${properties.kcFormClass!} paxl" action="${url.registrationAction}" method="post">
            <p class="red">
                <#if messagesPerField.existsError('password')>
                        ${msg("invalidPasswordMessage")}
                <#elseif messagesPerField.existsError('password-confirm')>
                        ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}
                </#if>
            </p>
            <p class="red">fields marked * are required.</p>
            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="firstName" class="${properties.kcLabelClass!}">${msg("firstName")}</label>
                    <b class="red">*</b>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="firstName" class="${properties.kcInputClass!}" name="firstName"
                           value="${(register.formData.firstName!'')}" required="required"
                           aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>"
                    />

                    <#if messagesPerField.existsError('firstName')>
                        <span id="input-error-firstname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('firstName'))?no_esc}
                        </span>
                    </#if>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="lastName" class="${properties.kcLabelClass!}">${msg("lastName")}</label>
                    <b class="red">*</b>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="lastName" class="${properties.kcInputClass!}" name="lastName"
                           value="${(register.formData.lastName!'')}" required="required"
                           aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>"
                    />

                    <#if messagesPerField.existsError('lastName')>
                        <span id="input-error-lastname" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('lastName'))?no_esc}
                        </span>
                    </#if>
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label for="email" class="${properties.kcLabelClass!}">${msg("email")}</label>
                    <b class="red">*</b>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="email" class="${properties.kcInputClass!}" name="email"
                           value="${(register.formData.email!'')}" autocomplete="email" required="required"
                           aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"
                    />

                    <#if messagesPerField.existsError('email')>
                        <span id="input-error-email" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                            ${kcSanitize(messagesPerField.get('email'))?no_esc}
                        </span>
                    </#if>
                </div>
            </div>

            <#if !realm.registrationEmailAsUsername>
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="username" class="${properties.kcLabelClass!}">${msg("username")}</label>
                        <b class="red">*</b>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="text" id="username" class="${properties.kcInputClass!}" name="username"
                               value="${(register.formData.username!'')}" autocomplete="username"
                               aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"
                        />

                        <#if messagesPerField.existsError('username')>
                            <span id="input-error-username" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                ${kcSanitize(messagesPerField.get('username'))?no_esc}
                            </span>
                        </#if>
                    </div>
                </div>
            </#if>

            <#if passwordRequired??>
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
                        <b class="red">*</b>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="password" id="password" class="${properties.kcInputClass!}" name="password"
                               autocomplete="new-password" required="required"
                               aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"
                        />
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="password-confirm"
                               class="${properties.kcLabelClass!}">${msg("passwordConfirm")}</label>
                               <b class="red">*</b>
                    </div>
                    <div class="${properties.kcInputWrapperClass!}">
                        <input type="password" id="password-confirm" class="${properties.kcInputClass!}"
                               name="password-confirm" required="required"
                               aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"
                        />
                    </div>
                </div>
                <div class="form-group">
                    <div class="alert alert-info" id="pw-desc">
                        ${msg("passwordRequirements")}
                    </div>
                </div>
            </#if>


            <label for="agree" class="checkbox">
                    <input type="checkbox" name="terms_agree" id="agree">
                        I agree to receive email updates and offers about Giftcards.com products and Giftcards.com third-party partner offers.
            </label>

            <#if recaptchaRequired??>
                <div class="form-group">
                    <div class="${properties.kcInputWrapperClass!}">
                        <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                    </div>
                </div>
            </#if>

            <div class="${properties.kcFormGroupClass!}">            
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doRegister")}"/>
            </div>
        </form>
        </div>
            </div>
          </div>
        </div>
    </#if>
</@layout.registrationLayout>