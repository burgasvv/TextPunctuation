<jsp:useBean id="punctuation" scope="request" type="java.lang.String"/>
<jsp:useBean id="amount" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="text" scope="request" type="java.lang.String"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Text</title>
</head>
<body>
    <div style="margin: 100px">
        <h1>Text: </h1>
        <h3>${text}</h3>
        <h1> Punctuation Series:
            <h2>${punctuation}</h2>
        </h1>
        <h1>Amount of Character Series: ${amount}</h1>
        <h1>Maximal length punctuation cookie: ${cookie.maxLengthPunctuation.value}</h1>
    </div>
</body>
</html>
