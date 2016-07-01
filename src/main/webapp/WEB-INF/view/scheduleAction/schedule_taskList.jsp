﻿<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title></title>
	<link rel="stylesheet" type="text/css" href="<%=basePath %>skins/main.css">
</head>
<body class="minW">
	<div class="space">
		<!-- 标题 -->
		<div class="title">
			<h1>司机任务动态列表</h1>
		</div>
		<div class="editBlock search">
		<s:form action="driver_taskList" method="post">
			<table>
				<tr>
					<th>驻车点</th>
					<td>						
						<s:select style="width:135px;" name="servicePointId" list="servicePointList" listKey="id" listValue="name"/>
					</td>
					<th>司机</th>
					<td>
						<s:textfield class="userSelector inputChoose inputText" id="driverName" type="text" name="driverName"/>
						<s:textfield id="driverId" name="driverId" type="hidden"/>
					</td>
					<th>车牌号</th>
					<td>
						<s:textfield id="car_platenumber" class="inputText inputChoose" onfocus="this.blur();" name="plateNumber" type="text" />
					</td>
					<th>时间</th>
					<td>
						<s:textfield name="beginDate" class="Wdate half" type="text" onfocus="new WdatePicker({dateFmt:'yyyy-MM-dd'})" />
						- 
						<s:textfield name="endDate" class="Wdate half" type="text" onfocus="new WdatePicker({dateFmt:'yyyy-MM-dd'})" />
					</td>
					<td>
                        <s:submit class="inputButton" value="查询"></s:submit>
					</td>
				</tr>
			</table>
			</s:form>
		</div>
		<div class="dataGrid">
			<div class="tableWrap fixW">
				<table>
					<colgroup>
						<col width="80"></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
						<col></col>
					</colgroup>
					<thead>
						<tr>
							<th>姓名</th>
							<th>车牌</th>
							<th>联系方式</th>
							<th>驻车点</th>	
							<c:set var="flag" value="0"></c:set>
							<c:forEach items="${recordList}" var="temp">							
  								<c:forEach items="${temp}" var="map">  
  									<c:if test="${flag==0}">  															
  									<c:forEach items="${map.value}" var="mapValue">
  										<th class="alignCenter">${mapValue.key}</th>
  									</c:forEach>
  									<c:set var="flag" value="1"></c:set> 
  									</c:if>		 																		
  								</c:forEach>
  							</c:forEach>	 																										
						</tr>
					</thead>
					<tbody class="tableHover">
					<c:forEach items="${recordList}" var="temp">
							<tr>
  								<c:forEach items="${temp}" var="map">  
  									<td>${map.key.driver.name}</td>
									<td>${map.key.plateNumber}</td>
									<td>${map.key.driver.phoneNumber}</td>
									<td>${map.key.servicePoint.name}</td>								
  									<c:forEach items="${map.value}" var="mapValue">  										
  										<c:if test="${mapValue.value==0}">
  											<td><i class="icon-car maintenance"></i>保养中</td>
  										</c:if>
  										<c:if test="${mapValue.value==1}">
  											<td><i class="icon-car repair"></i>维修中</td>
  										</c:if>
  										<c:if test="${mapValue.value==2}">
  											<td><i class="icon-car check"></i>年审中</td>
  										</c:if>
  										<c:if test="${mapValue.value==3}">  																										
  											<td><a href="#"><i class="icon-car"></i>任务中</a>
  											<input type="hidden" id="carId"  value="${map.key.id}"/>
  											<input type="hidden" id="orderDate"  value="${mapValue.key}"/>
  											</td>
  										</c:if>
  										<c:if test="${mapValue.value==4}">
  											<td><i class="icon-car gray"></i>空闲</td>
  										</c:if>
  									</c:forEach>
  								</c:forEach>
  							</c:forEach>
  							</tr>							
					</tbody>
				</table>
			</div>
			<div class="pageToolbar">
			<%@ include file="/WEB-INF/view/public/pageView.jspf" %>
			<s:debug></s:debug>			   
			</div>
		</div>
	</div>
	<script type="text/javascript" src="<%=basePath%>js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>js/DatePicker/WdatePicker.js"></script>
	<script src="js/artDialog4.1.7/artDialog.source.js?skin=blue"></script>
	<script src="js/artDialog4.1.7/plugins/iframeTools.source.js"></script>	
	<script type="text/javascript" src="<%=basePath%>js/common.js"></script>	
	<script type="text/javascript">
		$(function(){			
			$(".icon-car").not(".gray").closest("a").click(function(){
				var carId=$("#carId").val();
				var orderDate=$("#orderDate").val();
				popup("订单详情","order_info.action?carId="+carId+"&orderDate="+orderDate,650,500,"orderDetail");
			});
		})
	</script>
</body>
</html>