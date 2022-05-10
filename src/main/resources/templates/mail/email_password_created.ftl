<html lang="en">
<head>
    <style type="text/css">
        p {
        }

        p.title {
            font-weight: bold;
        }

        p.headline span, .text-bold {
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
    <title>Password Created</title>
</head>
<body>
<p class="title">Registration/Account Creation</p>
<p class="headline"><span>Headline:</span> Password Created </p>
<p>Dear ${EMAIL_FULLNAME}</p>
<p>Your password has been created successfully</p>
<p>Please Click the link below to continue</p>
<p><a href="${CLIENT_URL}" target="_blank">${CLIENT_URL}</a></p>
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
            </div><!-- /content -->

        </td>
        <td></td>
    </tr>
</table><!-- /FOOTER -->

</body>
</html>