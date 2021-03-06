package com.yuqincar.action.previlege;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ModelDriven;
import com.yuqincar.action.common.BaseAction;
import com.yuqincar.domain.car.DriverLicense;
import com.yuqincar.domain.common.GenderEnum;
import com.yuqincar.domain.common.PageBean;
import com.yuqincar.domain.common.TreeNode;
import com.yuqincar.domain.privilege.Role;
import com.yuqincar.domain.privilege.User;
import com.yuqincar.domain.privilege.UserStatusEnum;
import com.yuqincar.domain.privilege.UserTypeEnum;
import com.yuqincar.service.privilege.DepartmentService;
import com.yuqincar.service.privilege.RoleService;
import com.yuqincar.service.privilege.UserService;
import com.yuqincar.utils.QueryHelper;

@Controller
@Scope("prototype")
public class UserAction extends BaseAction implements ModelDriven<User> {
	
	private User model = new User();
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private RoleService roleService;
	
	private Long departmentId;
	private Long[] selectedRoleIds;
	private String roleString;
	
	private String oldPassword = "";
	private String newPassword = "";
	
	private String selectorName;
	private boolean driverOnly;
	private String departments;
	
	private String licenseID;
	private Date expireDate;
	
	private String actionFlag;
	private GenderEnum gender;
	private String keyword;
	private UserStatusEnum userStatus;
	
	/** 查询 */
	public String queryList(){
		
		QueryHelper helper = new QueryHelper("User", "u");
		if(model.getName()!=null && !"".equals(model.getName()))
			helper.addWhereCondition("u.name like ?", "%"+model.getName()+"%");
		helper.addWhereCondition("u.department.name <> ?", "外派");
		helper.addWhereCondition("u.visible=1");
		helper.addOrderByProperty("u.id", false);
		helper.addOrderByProperty("u.name", true);
		//用户状态查询
		if(userStatus!=null)
			helper.addWhereCondition("u.status = ?", userStatus);
		PageBean pageBean = userService.getPageBean(pageNum, helper);	
		ActionContext.getContext().getValueStack().push(pageBean);
		ActionContext.getContext().getSession().put("userHelper", helper);
		return "list";
	}
	
	/** 列表 */
	public String list(){
		QueryHelper helper = new QueryHelper("User", "u");
		helper.addWhereCondition("u.department.name <> ?", "外派");
		helper.addWhereCondition("u.visible=1");
		helper.addOrderByProperty("u.id", false);
		PageBean<User> pageBean = userService.getPageBean(pageNum, helper);	
		ActionContext.getContext().getValueStack().push(pageBean);
		ActionContext.getContext().getSession().put("userHelper", helper);
		return "list";
	}
	
	public String freshList() throws Exception {
		QueryHelper helper = (QueryHelper)ActionContext.getContext().getSession().get("userHelper");
		PageBean<User> pageBean = userService.getPageBean(pageNum, helper);	
		ActionContext.getContext().getValueStack().push(pageBean);
		return "list";
	}
	
	public String popup() {
		TreeNode nodes ;
		nodes= userService.getUserTree(model.getName(), driverOnly,departments);
		Gson gson = new Gson();
		ActionContext.getContext().put("nodes", gson.toJson(nodes));
		return "popup";
	}
	
	public void userAutocompleteRequest() {
		QueryHelper helper = new QueryHelper(User.class, "u");
		helper.addWhereCondition(
				"u.name like ?", "%" + keyword + "%");
		List<Object> list = new ArrayList<Object>();
		for (User user : userService.queryUser(1, helper)
				.getRecordList()) {
			list.add(user.getName());
		}
		JSONArray jsonArray = new JSONArray(list);
		writeJson(jsonArray.toJSONString());
	}

	/** 删除 */
	public String delete() throws Exception {
		if(model.isVisible())//不能删除不可见的用户
			userService.delete(model.getId());
		return freshList();
	}

	/** 添加页面 */
	public String addUI() throws Exception {
		System.out.println(actionFlag);
		ActionContext.getContext().put("actionFlag", actionFlag);
		// 准备数据：departmentList
		ActionContext.getContext().put("departmentList", departmentService.getAll());

		// 准备数据：roleList
		List<Role> roleList = roleService.getAllCompanyNull();
		ActionContext.getContext().put("roleList", roleList);
		List<Role> selectedList = new ArrayList<Role>();
		ActionContext.getContext().put("selectedList", selectedList);
		return "saveUI";
	}

	/** 添加 */
	public String add() throws Exception {
		if(userService.isLoginNameExist(0, model.getLoginName())){
			addFieldError("loginName", "登录名已经存在！");
			return addUI();
		}
		if(userService.isNameExist(0, model.getName())){
			addFieldError("name", "姓名已经存在！");
			return addUI();
		}
		if(model.getUserType() == UserTypeEnum.DRIVER){
			DriverLicense dl = new DriverLicense();
			dl.setLicenseID(licenseID);
			dl.setExpireDate(expireDate);
			model.setDriverLicense(dl);
		}
		model.setStatus(UserStatusEnum.NORMAL);//默认为正常状态
		model.setDepartment(departmentService.getById(departmentId));
		//处理角色
		if(roleString != null && !roleString.equals("")){
			String[] roleIds = roleString.split(",");
			Long[] longRoleIds = new Long[roleIds.length];
			for(int i=0;i<roleIds.length;i++){
				longRoleIds[i] = Long.parseLong(roleIds[i]);
			}	
			List<Role> roleList = roleService.getByIdsCompanyNull(longRoleIds);
			model.setRoles(new HashSet<Role>(roleList));
		}
		// 保存到数据库
		userService.save(model);
		ActionContext.getContext().getValueStack().push(new User());
		return freshList();
	}

	/** 修改页面 */
	public String editUI() throws Exception {
		ActionContext.getContext().put("actionFlag", actionFlag);
		// 准备回显的数据
		User user = userService.getById(model.getId());
		if(!user.isVisible())
			return null;
		if(user.getUserType() == UserTypeEnum.DRIVER){
			System.out.println("licenseID="+licenseID);
			licenseID = user.getDriverLicense().getLicenseID();
			expireDate = user.getDriverLicense().getExpireDate();
		}
		ActionContext.getContext().getValueStack().push(user);
		// 处理部门
		if (user.getDepartment() != null) {
			departmentId = user.getDepartment().getId();
		}
		// 处理岗位
		List<Role> selectedList = new ArrayList<Role>(user.getRoles());
		List<Role> roleList = roleService.getAllCompanyNull();
		selectedRoleIds = new Long[user.getRoles().size()];
		String selectedRoleString = "";
		int index = 0;
		for (Role role : user.getRoles()) {
			roleList.remove(role);
			selectedRoleString =selectedRoleString+ role.getId() + ",";
			selectedRoleIds[index++] = role.getId();
		}
		roleString = selectedRoleString;
		// 准备数据：departmentList
		ActionContext.getContext().put("departmentList", departmentService.getAll());

		// 准备数据：roleList
		ActionContext.getContext().put("roleList", roleList);
		ActionContext.getContext().put("selectedList", selectedList);
		return "saveUI";
	}

	/** 修改 */
	public String edit() throws Exception {
		if(userService.isLoginNameExist(model.getId(), model.getLoginName())){
			addFieldError("loginName", "登录名已经存在！");
			return addUI();
		}
		if(userService.isNameExist(model.getId(), model.getName())){
			addFieldError("name", "姓名已经存在！");
			return addUI();
		}
		model.setDepartment(departmentService.getById(departmentId));
		//处理角色
		if(roleString != null && !roleString.equals("")){
			String[] roleIds = roleString.split(",");
			Long[] longRoleIds = new Long[roleIds.length];
			for(int i=0;i<roleIds.length;i++){
				longRoleIds[i] = Long.parseLong(roleIds[i]);
			}
			List<Role> roleList = roleService.getByIdsCompanyNull(longRoleIds);
			System.out.println("roleList.size()"+roleList.size());
			model.setRoles(new HashSet<Role>(roleList));
		}
		
		if(model.getUserType()==UserTypeEnum.DRIVER){
			model.setDriverLicense(new DriverLicense());
			model.getDriverLicense().setLicenseID(licenseID);
			model.getDriverLicense().setExpireDate(expireDate);
		}
				
		//更新到数据库
		userService.update(model);
		ActionContext.getContext().getValueStack().push(new User());
		return freshList();
	}

	/** 初始化密码为1234 */
	public String initPassword() throws Exception {
		// 1，从数据库中取出原对象
		User user = userService.getById(model.getId());

		// 2，设置要修改的属性
		String md5 = DigestUtils.md5Hex("123456"); // 密码要使用MD5摘要
		user.setPassword(md5);

		// 3，更新到数据库
		userService.update(user);

		return "toList";
	} 
	
	public String info() {
		User user = (User) ActionContext.getContext().getSession().get("user");
		ActionContext.getContext().getValueStack().push(user);
		ActionContext.getContext().put("tabid", 1);		
		return "info";
	}
	
	public String changePassword() {
		User user = (User) ActionContext.getContext().getSession().get("user");
		user = userService.getById(user.getId());
		if(DigestUtils.md5Hex(oldPassword).equals(user.getPassword())) {
			user.setPassword(DigestUtils.md5Hex(newPassword));
			userService.update(user);
			ActionContext.getContext().getSession().put("user", user);
			ActionContext.getContext().put("pass_msg", "修改密码成功！");
		} else {
			ActionContext.getContext().put("pass_msg", "旧密码错误！");
		}
		ActionContext.getContext().put("tabid", 1);

			
		return "info";
	}
	
	public String changePhoneNumber() {
		User user = (User) ActionContext.getContext().getSession().get("user");
		user.setPhoneNumber(model.getPhoneNumber());
		userService.update(user);
		ActionContext.getContext().put("tabid", 2);
		ActionContext.getContext().put("phone_msg", "修改手机号码成功！");

		return "info";
	}
	
	public String queryDispatchList(){
		
		QueryHelper helper = new QueryHelper("User", "u");
		helper.addWhereCondition("u.department.name = ?", "外派");
		if(model.getName()!=null && !"".equals(model.getName()))
			helper.addWhereCondition("u.name like ?", "%"+model.getName()+"%");
		if(model.getPhoneNumber()!=null && !"".equals(model.getPhoneNumber()))
			helper.addWhereCondition("u.phoneNumber like ?", "%"+model.getPhoneNumber()+"%");
		helper.addWhereCondition("u.visible=1");
		helper.addOrderByProperty("u.id", false);
		PageBean<User> pageBean = userService.getPageBean(pageNum, helper);	
		ActionContext.getContext().getValueStack().push(pageBean);
		ActionContext.getContext().getSession().put("dispatchUserHelper", helper);
		
		return "dispatchList";
	}
	
	public String dispatchUserList(){		
		QueryHelper helper = new QueryHelper("User", "u");
		helper.addWhereCondition("u.department.name = ?", "外派");
		helper.addWhereCondition("u.visible=1");
		helper.addOrderByProperty("u.id", false);
		PageBean<User> pageBean = userService.getPageBean(pageNum, helper);	
		ActionContext.getContext().getValueStack().push(pageBean);
		ActionContext.getContext().getSession().put("dispatchUserHelper", helper);
		
		return "dispatchList";
	}
	
	public String freshDispatchList(){
		QueryHelper helper = (QueryHelper)ActionContext.getContext().getSession().get("dispatchUserHelper");
		PageBean<User> pageBean = userService.getPageBean(pageNum, helper);	
		ActionContext.getContext().getValueStack().push(pageBean);
		
		return "dispatchList";
	}
	
	public String addDispatchUI(){
		
		return "dispatchUI";
	}
	
	public String editDispatchUI(){
		
		User user = userService.getById(model.getId());
		ActionContext.getContext().getValueStack().push(user);
		return "dispatchUI";
	}
	
	public String editDispatchUser(){
		if(userService.isNameExist(model.getId(), model.getName())){
			addFieldError("name", "姓名已经存在！");
			return addDispatchUI();
		}
		User user = userService.getById(model.getId());
		user.setName(model.getName());
		user.setPhoneNumber(model.getPhoneNumber());
		userService.updateDispatchUser(user);
		ActionContext.getContext().getValueStack().push(new User());
		return freshDispatchList();
	}
	
	public String addDispatchUser(){
		if(userService.isNameExist(0, model.getName())){
			addFieldError("name", "姓名已经存在！");
			return addDispatchUI();
		}
		userService.saveDispatchUser(model.getName(),model.getPhoneNumber());
		ActionContext.getContext().getValueStack().push(new User());
		return freshDispatchList();
	}
	
	//判断能否删除
	public boolean isCanDeleteUser(){
		User user = (User) ActionContext.getContext().getValueStack().peek();
		if(userService.canDeleteUser(user.getId()))
			return true;
		else 
			return false;
	}
	
	/** 删除 */
	public String deleteDispatch() throws Exception {
		userService.delete(model.getId());
		ActionContext.getContext().getValueStack().push(new User());
		return freshDispatchList();
		
	}
	
	public String detail(){
		User user = (User) ActionContext.getContext().getSession().get("user");
		ActionContext.getContext().getValueStack().push(user);
		ActionContext.getContext().put("tabid", 3);		
		return "info";
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public User getModel() {
		return model;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getSelectorName() {
		return selectorName;
	}

	public void setSelectorName(String selectorName) {
		this.selectorName = selectorName;
	}

	public boolean isDriverOnly() {
		return driverOnly;
	}

	public void setDriverOnly(boolean driverOnly) {
		this.driverOnly = driverOnly;
	}
	
	

	public String getDepartments() {
		return departments;
	}

	public void setDepartments(String departments) {
		this.departments = departments;
	}

	public String getLicenseID() {
		return licenseID;
	}


	public void setLicenseID(String licenseID) {
		this.licenseID = licenseID;
	}


	public Date getExpireDate() {
		return expireDate;
	}


	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}


	public String getActionFlag() {
		return actionFlag;
	}


	public void setActionFlag(String actionFlag) {
		this.actionFlag = actionFlag;
	}

	public GenderEnum getGender() {
		return gender;
	}

	public void setGender(GenderEnum gender) {
		this.gender = gender;
	}

	public Long[] getSelectedRoleIds() {
		return selectedRoleIds;
	}

	public void setSelectedRoleIds(Long[] selectedRoleIds) {
		this.selectedRoleIds = selectedRoleIds;
	}

	public String getRoleString() {
		return roleString;
	}

	public void setRoleString(String roleString) {
		this.roleString = roleString;
	}

	public UserStatusEnum getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(UserStatusEnum userStatus) {
		this.userStatus = userStatus;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}