# Monzo widget

An Android app widget for your homescreen, showing the balance of your accounts or Pots.

## Getting started

Sign in to the [Monzo Developer Console](https://developers.monzo.com) and create a new _confidential_ client, and 
set the redirect url to `https://monzowidget`.

Create `gradle.properties` at the root of this project, and set the following values using your new client:

```
clientId=<your client id>
clientSecret=<your client secret>
```

## Login flow

| Login page | Redirect to Monzo | Request magic link email | Open magic link |
|---|---|---|---|
| ![step1](images/step1-login.png) | ![step2](images/step2-redirect.png) | ![step3](images/step3-reqmagiclink.png) | ![step4](images/step4-openmagiclink.png) |

| Log in | 2FA | Done! |
|---|---|---|
| ![step5](images/step5-auth.png) | ![step6](images/step6-sca.png) | ![step7](images/step7-done.png) |
