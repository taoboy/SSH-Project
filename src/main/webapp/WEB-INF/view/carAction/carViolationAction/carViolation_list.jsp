<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/view/common/common.jsp" %>
<cqu:border>
	<div class="space">
		<!-- 标题 -->
		<div class="title">
			<h1>违章信息列表</h1>
		</div>
		<div class="editBlock search">
		<s:form id="queryForm" action="carViolation_queryForm">
			<table>
				<tr>
					<td>
						<s:a cssClass="buttonA" action="carViolation_saveUI">违章登记</s:a>
					</td>
					<th><s:property value="tr.getText('car.CarViolation.car')" /></th>
					<td>
						<cqu:carAutocompleteSelector name="car"/>
					</td>
					<th>从</th>
					<td>
						<s:textfield name="beginDate" id="beginDate" class="Wdate half" type="text" onfocus="new WdatePicker({dateFmt:'yyyy-MM-dd'})" />
					</td>
					<th>到</th>
					<td>
						<s:textfield name="endDate" id="endDate" class="Wdate half" type="text" onfocus="new WdatePicker({dateFmt:'yyyy-MM-dd'})" />
					</td>
					<th>已经处理</th>
					<td>
					 <s:select name="isDeal" list="#{'0':'否','1':'是','2':'全部'}"  headerKey="0" >
					 </s:select>
					</td>
					<td>
						<input class="inputButton" type="submit" value="查询"/>
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
							<th>车牌号</th>
							<th>司机</th>
							<th>时间</th>
							<th>地点</th>
							<th>违章事实</th>
							<th>罚分</th>
							<th>罚款（元）</th>
							<th>是否已经处理</th>
							<th>处理日期</th>
							<th class="alignCenter">操作</th>
						</tr>
					</thead>
					<tbody class="tableHover">
						<s:iterator value="recordList">
				        
						<tr>
							<td><cqu:carDetailList id="${car.id}"/></td>
							<td>${driver.name}</td>
							<td ><s:date name="date" format="yyyy-MM-dd HH:mm"/></td>
							<td width="15%">${place}</td>
							<td width="20%">${description }</td>
							<td >${penaltyPoint }</td>
							<td >${penaltyMoney }</td>
							<td >
							<s:if test="dealt==true">
								<s:text name="是"></s:text>
								</s:if>
								<s:else>
								<s:text name="否"></s:text>
								</s:else>
							</td>
							<td ><s:date name="dealtDate" format="yyyy-MM-dd"/></td>
							<td>
                    			<s:a action="carViolation_delete?id=%{id}" onclick="result=confirm('确认要删除吗？'); if(!result) coverHidden(); return result;"><i class="icon-operate-delete" title="删除"></i></s:a>
                    			<s:if test="canUpdateCarViolation">
                    			<s:a action="carViolation_editUI?id=%{id}"><i class="icon-operate-edit" title="修改"></i></s:a>
                    			</s:if>
          					</td> 
						</tr>
						</s:iterator> 
						
					</tbody>
				</table>
			</div>
			 <s:form id="pageForm" action="carViolation_freshList">
				<%@ include file="/WEB-INF/view/public/pageView.jspf" %>
			</s:form>
		</div>
	</div>
	<script type="text/javascript">
		$(function(){
			formatDateField2($("#beginDate"));
			formatDateField2($("#endDate"));
		})
		
	</script>
</cqu:border>