<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/common/common.jsp" %>
<cqu:border>
	<div class="space">
		<!-- 标题 -->
		<div class="title">
			<h1>车辆维修</h1>
		</div>		
		<div class="tab_next style2">
			<table>
				<tr>
				    <td><s:a action="carRepairAppointment_list"><span>预约车辆维修</span></s:a></td>
					<td class="on"><a href="#" class="coverOff"><span>车辆维修</span></a></td>
				</tr>
			</table>
		</div>
		<br/>
		<div class="editBlock search">
			<s:form id="pageForm" action="carRepair_queryForm">
			<table>
				<tr>
					<td>
						<s:a cssClass="buttonA" action="carRepair_addUI">维修登记</s:a>
					</td>
					<th><s:property value="tr.getText('car.CarRepair.car')" /></th>
					<td>
						<cqu:carAutocompleteSelector name="car"/>
					</td>
					<th><s:property value="tr.getText('car.CarRepair.driver')" /></th>
					<td>
						<cqu:userAutocompleteSelector name="driver"/>
					</td>
					<th>从</th>
					<td>
						<s:textfield name="date1" id="date1" class="Wdate half" type="text" onfocus="new WdatePicker({dateFmt:'yyyy-MM-dd'})" />
					</td>
					<th>到</th>
					<td>
						<s:textfield name="date2" id="date2" class="Wdate half" type="text" onfocus="new WdatePicker({dateFmt:'yyyy-MM-dd'})" />
					</td>
					<td>
						<input class="inputButton" type="submit" value="查询"/>
					</td>
					<td>
						<s:a cssClass="buttonA" action="carRepair_excel">维修信息导入</s:a>
					</td>
					<td>
					 	<s:if test="carId!=null">
							<a class="p15" href="javascript:history.go(-1);">返回</a>
						</s:if>
					</td>
				</tr>
			</table>
			</s:form>
		</div>
		<div class="dataGrid">
			<div class="tableWrap">
				<table>
					
					<thead>
						<tr>
							<th><s:property value="tr.getText('car.CarRepair.car')" /></th>
							<th><s:property value="tr.getText('car.CarRepair.driver')" /></th>
              				<th>维修时间</th>
              				<th><s:property value="tr.getText('car.CarRepair.repairLocation')" /></th>
              				<th><s:property value="tr.getText('car.CarRepair.reason')" /></th>
                			<th><s:property value="tr.getText('car.CarRepair.money')" />(元)</th>
                			<th><s:property value="tr.getText('car.CarRepair.moneyNoGuaranteed')" />(元)</th>
                			<th><s:property value="tr.getText('car.CarRepair.memo')" /></th>
                			<th>操作</th>
						</tr>
					</thead>
					<tbody class="tableHover">
				        <s:iterator value="recordList">
						<tr>
							<td><cqu:carDetailList id="${car.id}"/></td>
							<td>${driver.name}</td>
							<td style="text-align:right"><s:date name="fromDate" format="yyyy-MM-dd"/>&nbsp;&nbsp;&nbsp;&nbsp;<s:date name="toDate" format="yyyy-MM-dd"/></td>
							<td>${repairLocation}</td>
							<td width="15%">${reason}</td>
							<td style="text-align:right"><fmt:formatNumber value="${money}" pattern="#0"/></td>
							<td style="text-align:right"><fmt:formatNumber value="${moneyNoGuaranteed}" pattern="#0"/></td>
							<td width="25%">${memo}</td>
							<td>
                    			<s:a action="carRepair_delete?id=%{id}" onclick="result=confirm('确认要删除吗？'); if(!result) coverHidden(); return result;"><i class="icon-operate-delete" title="删除"></i></s:a>
                    			<s:a action="carRepair_editUI?id=%{id}"><i class="icon-operate-edit" title="修改"></i></s:a>
                			</td>
						</tr>
						</s:iterator> 
					</tbody>
				</table>
			</div>
			<s:form id="pageForm" action="carRepair_refreshList">
			<%@ include file="/WEB-INF/view/public/pageView.jspf" %>
			</s:form>
		</div>
	</div>
	
	<script type="text/javascript">
		$(function(){
			formatDateField2($("#date1"));
			formatDateField2($("#date2"));
	    })
	</script>
</cqu:border>
