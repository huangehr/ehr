<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="contextRoot" value="<%=request.getContextPath()%>" scope="page" />
<c:set var="devgMode" value="true" scope="page" />

<c:choose>
  <c:when test="${devgMode == 'true'}">
    <c:set var="staticRoot" value="${contextRoot}/develop" scope="page" />
  </c:when>
  <c:otherwise>
    <c:set var="staticRoot" value="${contextRoot}/static" scope="page" />
  </c:otherwise>
</c:choose>