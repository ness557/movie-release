<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Movie release notification</title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700&amp;subset=cyrillic" rel="stylesheet">

    <!-- use the font -->
    <style>
        body {
            font-family: 'Open Sans', sans-serif;
            font-weight: bold;
            font-size: 20px;

        }
    </style>
</head>
<body style="margin: 0; padding: 0;">

<table align="center" border="0" cellpadding="0" cellspacing="0" width="500" style="border-collapse: collapse;">
    <tr>
        <td style="padding-top: 15px; text-align: center; padding-bottom: 20px; color:#fff;background-color:#337ab7;border-color:#2e6da4;border-radius:10px">
            <a style="color:#fff;" href="${showLink}">${show}</a><span> </span>
            <a style="color:#fff;" href="${seasonLink}">Season ${seasonNum}</a> was released at ${releaseDate}
            <br>
        </td>
    </tr>
    <tr>
        <td style="padding-top: 5px">
            <img src="${poster}" width="500"/>
        </td>
    </tr>
</table>

</body>
</html>