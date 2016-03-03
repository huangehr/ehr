<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">

<body>
	<c:url var="url"/>
	<spring:url value="/resources/text.txt" htmlEscape="true" var="springUrl" />
	<br>
	JSTL URL: ${url}
	<br>
</body>

</html>
