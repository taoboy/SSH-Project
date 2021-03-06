package com.yuqincar.service.sms.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.yuqincar.domain.common.Company;
import com.yuqincar.domain.message.SMSFailRecord;
import com.yuqincar.domain.message.SMSQueue;
import com.yuqincar.domain.message.SMSRecord;
import com.yuqincar.service.message.SMSQueueService;
import com.yuqincar.service.sms.SMSFailRecordService;
import com.yuqincar.service.sms.SMSRecordService;
import com.yuqincar.service.sms.SMSService;
import com.yuqincar.utils.Configuration;
import com.yuqincar.utils.DateUtils;
import com.yuqincar.utils.HttpInvoker;
import com.yuqincar.utils.RandomUtil;
import com.yuqincar.utils.SMSToken;

@Service
public class SMSServiceImpl implements SMSService {
		
	private static String SMS_GATE_URL="http://api.189.cn/v2/emp/templateSms/sendSms";
	private static String APP_ID = "593884530000249253";//应用ID------登录平台在应用设置可以找到
	private static String APP_SECRET = "ba1a8529eef141b3d5b631d998f33fd0";//应用secret-----登录平台在应用设置可以找到
	private static int SMS_TRY_TIMES = 6;
	//private static String SEND_SUCCESS_TEMPLATE="{\"res_code\":0,\"res_message\":\"Success\",\"idertifier\":\"90610313152615759721\"}";

	
	//短信模板ID与短信模板内容的对应关系
	public static Map<String, String> SMS_CONTENT;
	static {
		SMS_CONTENT=new HashMap<String,String>();
		SMS_CONTENT.put(SMS_TEMPLATE_MILE_ORDER_ACCEPTED, "尊敬的customerSurname老师，您好！我是重庆市党政机关过渡性车辆服务中心（重庆市渝勤汽车服务有限公司）调度schedulerName，很高兴为您服务。planBeginDate，为您服务的驾驶员driverName，电话：driverPhoneNumber，车号：plateNumber。驾驶员会提前与你联系。服务监督电话60391609。夜间与周末用车服务电话60391610。谢谢！");
		SMS_CONTENT.put(SMS_TEMPLATE_DAY_ORDER_ACCEPTED,"尊敬的customerSurname老师，您好！我是重庆市党政机关过渡性车辆服务中心（重庆市渝勤汽车服务有限公司）调度schedulerName，很高兴为您服务。planBeginDate，为您服务的驾驶员driverName，电话：driverPhoneNumber，车号：plateNumber。驾驶员会提前与你联系。服务监督电话60391609。夜间与周末用车服务电话60391610。谢谢！");
		SMS_CONTENT.put(SMS_TEMPLATE_EXAMINE_APPOINTMENT,"您已经预约了一次车辆年审。年审时间为：carExamineDate。请在年审结束之后及时联系后勤管理人员登记具体年审信息。");
		SMS_CONTENT.put(SMS_TEMPLATE_REPAIR_APPOINTMENT,"您已经预约了一次车辆维修。维修时间为：carRepairDate。请在维修结束之后及时联系后勤管理人员登记具体维修信息。");
		SMS_CONTENT.put(SMS_TEMPLATE_VERFICATION_CODE,"尊敬的param1用户，您的验证码为param2，感谢您的使用！");
		SMS_CONTENT.put(SMS_TEMPLATE_ORDER_POSTPONE,"您的订单（sn）结束时间已经延后。原时间：oriDate；延后到：newDate。");
		SMS_CONTENT.put(SMS_TEMPLATE_ORDER_CANCELLED,"您的订单（sn）已经被取消。原因：reason。");
		SMS_CONTENT.put(SMS_TEMPLATE_ORDER_ENQUEUE,"有新订单入队列。customer（customerOrganization，phoneNumber），chargeMode，time，address。");
		SMS_CONTENT.put(SMS_TEMPLATE_NEW_ORDER,"您有新的订单任务。单位：customerOrganization，联系人：customerName， 联系电话：phoneNumber，用车时间：time，目的地：destination，客户要求：customerMemo。请接受！");
		SMS_CONTENT.put(SMS_TEMPLATE_NEW_ORDER_INCLUDE_OTHER_PASSENGER,"您有新的订单任务。单位：customerOrganization，联系人：customerName， 联系电话：phoneNumber，实际乘车人：otherPassengerName（otherPhoneNumber），用车时间：time，目的地：destination，客户要求：customerMemo。请接受！");
		SMS_CONTENT.put(SMS_TEMPLATE_CARCARE_APPOINTMENT_GENERATED_FOR_DRIVER,"车辆plateNumber达到了保养里程。系统已经为您生成了保养预约，日期是date，请保养车辆。保养完成后，请及时通知后勤保障科同事，以便解除车辆锁定。在此之前，调度人员无法为该车调度订单。");
		SMS_CONTENT.put(SMS_TEMPLATE_CARCARE_APPOINTMENT_GENERATED_FOR_MANAGER,"有车辆达到了保养里程，系统已经生成了保养预约，请关注。车辆（预约日期）如下：plateNumber");
		SMS_CONTENT.put(SMS_TEMPLATE_CARCARE_APPOINTMENT_GENERATED_NO_DRIVER,"车辆plateNumber达到了保养里程。但由于该车辆没有指定默认驾驶员，无法自动生成保养预约记实录。所以需要您联系驾驶员前去做保养，并在系统中手动添加保养预约记录，否则无法锁定驾驶员，有可能导致订单调度错误。");
		SMS_CONTENT.put(SMS_TEMPLATE_CARCARE_APPOINTMENT_GENERATED_FOR_4S_EMPLOYEE,"有车辆预约保养，请关注。车辆（预约日期）如下：plateNumber");
		SMS_CONTENT.put(SMS_TEMPLATE_CARCARE_NEARBY,"车辆plateNumber还有大约mileage公里将达到保养里程数，请根据实际情况合理安排工作。保养车辆前，请一定联系保养管理员创建保养预约。");
		SMS_CONTENT.put(SMS_TEMPLATE_RESERVECARAPPLYORDER_SUBMITTED_FOR_APPROVEUSER,"有一个proposer提交的临时扩充常备车库申请需要您审核");
		SMS_CONTENT.put(SMS_TEMPLATE_RESERVECARAPPLYORDER_REJECTED_FOR_PROPOSER,"您提交的临时扩充常备车库申请被approveUser驳回。原因：reason。");
		SMS_CONTENT.put(SMS_TEMPLATE_RESERVECARAPPLYORDER_APPROVED_FOR_PROPOSER_CARAPPROVEUSER_DRIVERAPPROVEUSER,"您有新的临时扩充常备车库申请被approveUser审核通过。");
		SMS_CONTENT.put(SMS_TEMPLATE_RESERVECARAPPLYORDER_CARAPPROVED_FOR_PROPOSER,"您提交的临时扩充常备车库申请已由carApproveUser配置车辆完成。");
		SMS_CONTENT.put(SMS_TEMPLATE_RESERVECARAPPLYORDER_DRIVERAPPROVED_FOR_PROPOSER,"您提交的临时扩充常备车库申请已由driverApproveUser配置司机完成。");
		SMS_CONTENT.put(SMS_TEMPLATE_ORDER_END,"订单（sn）已经执行完毕。单位：customerOrganization，车辆：plateNumber，驾驶员：driver。");
		SMS_CONTENT.put(SMS_TEMPLATE_RESCHEDULE,"您的订单已经重新调度。车辆：plateNumber，司机：driverName（phoneNumber）。");
	
	}

	
	
	@Autowired
	private SMSQueueService sMSQueueService;
	
	@Autowired
	private SMSRecordService sMSRecordService;
	
	@Autowired
	private SMSFailRecordService sMSFailRecordService;
	/*
	private void sendSMSToFile(String phoneNumber, String templateId, String content) {
		File file = new File(Configuration.getSmsLogFile());
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(file, true));
			pw.print(phoneNumber);
			pw.println();
			pw.print(templateId);
			pw.println();
			pw.println(content);
			pw.println();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}
	*/
	
	private String sendTemplateSMS(String phoneNumber,String templateId, String paramString) throws Exception{
		String postEntity = "app_id=" + APP_ID + "&access_token="
				+ SMSToken.getSMSToken() + "&acceptor_tel=" + phoneNumber + "&template_id="
				+ templateId + "&template_param=" + paramString
				+ "&timestamp=" + URLEncoder.encode(DateUtils.getYMDHMSString(new Date()), "utf-8");
		String resJson = "";
		//验证码短信不需要打开开关。这是为了使测试服务器能够正常发送验证码。
		if("on".equals(Configuration.getSmsSwitch()) || "13883101475".equals(phoneNumber) || templateId.equals(SMSService.SMS_TEMPLATE_VERFICATION_CODE)){
			resJson = HttpInvoker.httpPost1(SMS_GATE_URL, null, postEntity);
		}
		
		return resJson;
	}

	@Transactional
	private void AddSMSRecord(String phoneNumber,String templateId, String paramString, Company company)
	{
		SMSRecord sr=new SMSRecord();
		sr.setDate(new Date());
		sr.setContent(getSMSRecordContent(templateId, paramString));
		sr.setPhoneNumber(phoneNumber);
		sr.setCompany(company);
		sMSRecordService.saveSMSRecord(sr);
	}
	
	protected String getSMSRecordContent(String templateId,String paramString) {
		String content=SMS_CONTENT.get(templateId);		
		Gson gson = new Gson();
		Map<String,String> map=gson.fromJson(paramString, Map.class); /*将内容转化成Map*/
		for(String key:map.keySet())
			content=content.replaceAll(key, map.get(key));
		return content;
	}
	
	@Transactional
	private void AddSMSFailRecord(String phoneNumber,String templateId, String paramString, Company company, String errorMemo)
	{
		//System.out.println("###smsTemplate="+content);
		SMSFailRecord sr=new SMSFailRecord();
		sr.setDate(new Date());
		sr.setContent(getSMSRecordContent(templateId,paramString));
		sr.setPhoneNumber(phoneNumber);
		sr.setCompany(company);
		sr.setErrorMemo(errorMemo);
		sMSFailRecordService.saveSMSFailRecord(sr);
	}
	
	private List<String> chopSMSContent(String content){
		int length=201;
		List<String> list=new ArrayList<String>();
		while(content.length()>0){
			if(content.length()>length){
				list.add(content.substring(0, length));
				content=content.substring(length);
			}else{
				list.add(content);
				content="";
			}
		}
		return list;
	}
	
	@Transactional
	public void sendTemplateSMS(String phoneNumber,String templateId, Map<String, String> params) {
		Gson gson = new Gson();
		String template_param = gson.toJson(params);	
		for(String content:chopSMSContent(template_param)){  //对于过长的短信内容自动分成多条发送。
			SMSQueue sq=new SMSQueue();
			sq.setPhoneNumber(phoneNumber);
			sq.setTemplateId(templateId);
			sq.setParams(content);
			sq.setInQueueDate(new Date());
			sq.setTryTimes(0);
			sMSQueueService.saveSMSQueue(sq);
		}
	}
	
	@Transactional	
	public void sendInstantTemplateSMS(String phoneNumber,String templateId,Map<String,String> params){
		Gson gson = new Gson();
		String template_param = gson.toJson(params);
		
		try{
			String resJson=sendTemplateSMS(phoneNumber,templateId,template_param);
			if(!resJson.toLowerCase().contains("success")){
				SMSQueue sq=new SMSQueue();
				sq.setPhoneNumber(phoneNumber);
				sq.setTemplateId(templateId);
				sq.setParams(template_param);
				sq.setInQueueDate(new Date());
				sq.setTryTimes(0);
				sMSQueueService.saveSMSQueue(sq);
			}else{
				AddSMSRecord(phoneNumber,templateId,template_param,null);
			}
		}catch(Exception e){
			e.printStackTrace();
			SMSQueue sq=new SMSQueue();
			sq.setPhoneNumber(phoneNumber);
			sq.setTemplateId(templateId);
			sq.setParams(template_param);
			sq.setInQueueDate(new Date());
			sq.setTryTimes(0);
			sMSQueueService.saveSMSQueue(sq);
		}
	}
	
	@Transactional
	public void sendSMSInQueue(){
		List<SMSQueue> smsList=sMSQueueService.getAllSMSQueue();
		for(SMSQueue sms:smsList){
			try{
				String resJson=sendTemplateSMS(sms.getPhoneNumber(),sms.getTemplateId(),sms.getParams());
				if(resJson.toLowerCase().contains("success")){
					sMSQueueService.deleteSMSQueue(sms.getId());
					AddSMSRecord(sms.getPhoneNumber(),sms.getTemplateId(),sms.getParams(),sms.getCompany());
				}else{
					sms.setTryTimes(sms.getTryTimes()+1);
					if(sms.getTryTimes()>=SMS_TRY_TIMES)
					{
						sMSQueueService.deleteSMSQueue(sms.getId());
						AddSMSFailRecord(sms.getPhoneNumber(),sms.getTemplateId(),sms.getParams(),sms.getCompany(),resJson.toString());
					}
					else
						sMSQueueService.updateSMSQueue(sms);
				}
			}catch(Exception e){
				sms.setTryTimes(sms.getTryTimes()+1);
				if(sms.getTryTimes()>=SMS_TRY_TIMES)
				{
					sMSQueueService.deleteSMSQueue(sms.getId());
					AddSMSFailRecord(sms.getPhoneNumber(),sms.getTemplateId(),sms.getParams(),sms.getCompany(),e.getMessage());
				}
				else
					sMSQueueService.updateSMSQueue(sms);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Map<String,String> map = new HashMap<String,String>();
			map.put("param1", "手机");
			map.put("param2", RandomUtil.randomFor6());
			new SMSServiceImpl().sendInstantTemplateSMS("13883101475",SMSService.SMS_TEMPLATE_VERFICATION_CODE,map);
			Gson gson = new Gson();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
