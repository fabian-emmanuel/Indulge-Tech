<html>
<head>
    <style type="text/css">
        p {
        }

        p.title {
            font-weight: bold;
        }

        p.headline span {
            font-weight: bold;
        }

        /* -------------------------------------
               FOOTER
       ------------------------------------- */
        table.footer-wrap {
            width: 100%;
            clear: both !important;
        }

        .footer-wrap .container td.content p {
            border-top: 1px solid rgb(215, 215, 215);
            padding-top: 15px;
        }

        .footer-wrap .container td.content p {
            font-size: 10px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<p class="title">Registration/Account Creation</p>
<p class="headline"><span>Headline:</span> New Account Confirmation </p>
<p>Dear ${EMAIL_FULLNAME}</p>
<p>Welcome to Indulge Tech. Your account has been created, and you have been issued a temporary password.</p>
<p>Please Click the link below to confirm your account and create your password</p>
<p><a href="${CREATE_PASSWORD_URL}" target="_blank">${CREATE_PASSWORD_URL}</a></p>
<p>Best Regards,</p>

<!-- FOOTER -->
<table class="footer-wrap">
    <tr>
        <td></td>
        <td class="container">
            <!-- content -->
            <div class="content">
                <p>
                    <table>
                        <tr>
                            <td align="center">
                <p>
                    ${EMAIL_DISCLAIMER}
                </p>
                <p>
                    ${EMAIL_SPAM_DISCLAIMER}
                </p>
                <p>
                    ${EMAIL_FOOTER_COPYRIGHT}
                </p>
        </td>
    </tr>
</table>
</p>
</div><!-- /content -->

</td>
<td></td>
</tr>
</table><!-- /FOOTER -->

</body>
</html>