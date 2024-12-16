window.onload = onLoad;

const MOTD_ENVIRONMENTS = Object.freeze({
   "auth.cern.ch": "https://sso-motd-api.web.cern.ch/api/motd/",
   "keycloak-qa.cern.ch": "https://sso-motd-api-qa.web.cern.ch/api/motd/",
   "default": "https://test-sso-motd-api.web.cern.ch/api/motd/"
});

const FORGOTTEN_PASS_URL_ENVS = Object.freeze({
   "keycloak-dev.cern.ch": "https://test-users-portal.web.cern.ch/self-service-reset",
   "keycloak-qa.cern.ch": "https://users-portal-qa.web.cern.ch/self-service-reset",
   "default": "https://users-portal.web.cern.ch/self-service-reset"
});   



function getUrl(env, host) {
   if (env.hasOwnProperty(host)) {
      return env[host];
   } else {
      return env["default"];
   }
}

function onLoad() {
   const urlParams = new URLSearchParams(window.location.search);
   const only2fa = urlParams.has('only2fa') && urlParams.get('only2fa') != 'false';
   const host = window.location.hostname;
   const motdUrl = getUrl(MOTD_ENVIRONMENTS, host);
   const forgottenPassUrl = getUrl(FORGOTTEN_PASS_URL_ENVS, host);

   if (only2fa) redirectTo2FA();
   if (document.getElementById('alert-security') && document.getElementById('security-motd')) {
      loadMotd(motdUrl);
   }
   insertResetPassowordUrl(forgottenPassUrl);
   if (document.getElementById("username-email-help")) {
      document.getElementById("username").onchange = function() {validateUsername()};
   }
}

function redirectTo2FA() {
   const url = new URL(window.location.href);
   url.searchParams.delete('only2fa');
   url.searchParams.set('kc_idp_hint', 'mfa');
   window.location.replace(url.toString());
}

async function loadMotd(theUrl) {
   const objResponse = await loadJson(theUrl);
   if (objResponse === null) {
      return;
   } else {
      const expires = new Date(objResponse.expires)
      const now = new Date();
      if (expires.getTime() > now.getTime()) {
         insertMotd(objResponse.message)
      }
   }
}

function insertMotd(motd) {
   if (motd != "") {
      document.getElementById('alert-security').style.removeProperty('display');
      document.getElementById('security-motd').innerHTML = motd;
   }
}

function insertResetPassowordUrl(passUrl) {
   element = document.getElementById('resetPassUrl');
   if (element && passUrl != "") {
      element.href = passUrl;
   }
}

async function loadJson(theUrl) {
   let request = new Request(theUrl);
   return fetch(request).then(async response => {
      if (response.ok) {
         const jsonObject = await response.json();
         return jsonObject;
      } else {
         return null;
      }
   });
}

function validateUsername() {
   const inputElement = document.getElementById("username");
   const username = document.getElementById("username").value;
   if (username.indexOf("@") > -1) {
      inputElement.setAttribute("aria-invalid", true);
      document.getElementById("username-email-help").style.display = "";
   } else {
      inputElement.removeAttribute("aria-invalid");
      document.getElementById("username-email-help").style.display = "none";
   }
   document.getElementById("username").onchange = function() {validateUsername()};
}
​