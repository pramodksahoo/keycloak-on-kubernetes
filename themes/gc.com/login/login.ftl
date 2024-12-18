<#import "template.ftl" as layout>

<script src="https://accounts.google.com/gsi/client"></script>

<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
        
    <#elseif section = "form">
    <div id="kc-form">
      <div id="kc-form-wrapper">
        <#if realm.password>
        <div class="rows">
          <div class="column-12" id="copy_full">
            <div class="rows">
              <h1 class="center-column column-6 mvxl">Sign in</h1>
              <div class="center-column column-6 mvxl border-gray-as mln">
                <ul id="login-nav" class="nav-base nav-tab two">
                  <li class="active two">
                    <a>${msg("loginAccountTitle")}</a>
                  </li>
                  <li class="two link-right">
                    <a href="${url.registrationUrl}">Register</a>
                  </li>
                </ul>
            <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post" class="form-wrapper paxl">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="form-group">
                        <label for="username" class="${properties.kcLabelClass!} required"><#if !realm.loginWithEmailAllowed>${msg("username")}<#elseif !realm.registrationEmailAsUsername>${msg("usernameOrEmail")}<#else>${msg("email")}</#if></label>

                        

                        <#if usernameEditDisabled??>
                            <input tabindex="1" id="username" class="${properties.kcInputClass!} form-control" name="username" value="${(login.username!'')}" type="text" disabled required="required" />
                        <#else>
                            <input tabindex="1" id="username" class="${properties.kcInputClass!} form-control" name="username" value="${(login.username!'')}" required="required" type="email" autofocus autocomplete="off"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                            />

                            
                        </#if>

                      </div>



                    
                <div class="${properties.kcFormGroupClass!}">
                    <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>

                    <input tabindex="2" id="password" class="${properties.kcInputClass!} form-control" name="password" type="password" required="required" autocomplete="off"
                           aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                    />
                </div>

                <div class="rows ptl">
                    <div class="column-2">
                          <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                          <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                    </div>
                  </div>

                <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
                    <div id="kc-form-options">
                        <#if realm.rememberMe && !usernameEditDisabled??>
                            <div class="checkbox">
                                <label>
                                    <#if login.rememberMe??>
                                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" checked> ${msg("rememberMe")}
                                    <#else>
                                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"> ${msg("rememberMe")}
                                    </#if>
                                </label>
                            </div>
                        </#if>
                        </div>
                        <div class="${properties.kcFormOptionsWrapperClass!}">
                            <#if realm.resetPasswordAllowed>
                                <p>
                                    <a href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a>
                                  </p>
                            </#if>
                        </div>

                    
                          <#if realm.password && social.providers??>
                            <div id="kc-social-providers" class="${properties.kcFormSocialAccountSectionClass!}">
                                <div class="div-line">
                                  <hr>
                                </div>
                                <div class="text-center">
                              <span class="div-text">OR</span>
                            </div>
                                <p>
                                    <strong>${msg("identity-provider-login-label")}</strong>
                                  </p>

                                <div class="${properties.kcFormSocialAccountListClass!} <#if social.providers?size gt 3>${properties.kcFormSocialAccountListGridClass!}</#if>">

                                    <#list social.providers as p>
                                        <a id="g_id_onload" class="${properties.kcFormSocialAccountListButtonClass!} <#if social.providers?size gt 3>${properties.kcFormSocialAccountGridItem!}</#if><#if p.displayName == 'Facebook'>btn-facebook-login</#if>"
                                                 href="${p.loginUrl}">
                                            <#if p.iconClasses?has_content>
                                                <span class="${properties.kcCommonLogoIdP!} ${p.iconClasses!}" aria-hidden="true"></span>
                                                <span class="float-left mts">${p.displayName!}</span>


                                            <#else>
                                                <span class="${properties.kcFormSocialAccountNameClass!}">${p.displayName!}</span>
                                            </#if>
                                        </a>


                                    </#list>

                                </ul>
                            </div>
                        </#if>

                  </div>

                  
            </form>
            </div>
            </div>
          </div>
        </div>
        </#if>
        </div>

        

    </div>
    <#elseif section = "info" >
        
    </#if>

</@layout.registrationLayout>
