<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/common/common.jsp" %>
<cqu:border>
	<div class="space">
		<!-- 标题 -->
		<div class="title">
			<h1>客户列表</h1>
		</div>
		<div class="editBlock search">
			<s:form id="pageForm" action="customer_queryList">
			<table>
				<tr>
					<td>
						<s:a cssClass="buttonA" action="customer_addUI">新增客户</s:a>
					</td>
					<th><s:property value="tr.getText('order.Customer.customerOrganization')" /></th>
					<td>
						<cqu:customerOrganizationSelector name="customerOrganization"/>
						 
					</td>
					<th><s:property value="tr.getText('order.Customer.name')" /></th>
					<td><s:textfield cssClass="inputText" name="name" type="text" /></td>
					<th><s:property value="tr.getText('order.Customer.phones')" /></th>
					<td><s:textfield cssClass="inputText" name="phone" type="text" /></td>
					<td>
						<input class="inputButton" type="submit" value="查询"/>
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
							<th><s:property value="tr.getText('order.CustomerOrganization.name')" /></th>
              				<th><s:property value="tr.getText('order.Customer.name')" /></th>
              				<th><s:property value="tr.getText('order.Customer.gender')" /></th>
              				<th><s:property value="tr.getText('order.Customer.phones')" /></th>
                			<th>操作</th>
						</tr>
					</thead>
					<tbody class="tableHover">
				        <s:iterator value="recordList">
						<tr>
							<td>${customerOrganization.name}</td>
							<td>${name}</td>
							<td>${gender.label}</td>
							<td>
								<s:iterator value="phones" var="p" status="s">
									<s:property value="p"/>
									<s:if test="!#s.last">
										,
									</s:if>
								</s:iterator>
                			</td>
							<td>
								<s:a action="customer_editUI?id=%{id}"><i class="icon-operate-edit" title="修改"></i></s:a>
								<s:if test="canDelete">
                    			<s:a action="customer_delete?id=%{id}" onclick="result=confirm('确认要删除吗？'); if(!result) coverHidden(); return result;"><i class="icon-operate-delete" title="删除"></i></s:a>
                    			</s:if>
                    			<s:else></s:else>
                			</td>
						</tr>
						</s:iterator> 
					</tbody>
				</table>
			</div>
			<s:form id="pageForm" action="customer_freshList">
			<%@ include file="/WEB-INF/view/public/pageView.jspf" %>
			</s:form>
		</div>
	</div>
	
	
	<script type="text/javascript">
		$(function(){
	    })
	</script>
</cqu:border>
