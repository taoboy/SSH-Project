package com.yuqincar.service.car;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yuqincar.domain.car.Car;
import com.yuqincar.domain.car.CarCare;
import com.yuqincar.domain.common.PageBean;
import com.yuqincar.domain.privilege.User;
import com.yuqincar.utils.QueryHelper;

public interface CarCareService {
	/**
	 * 增加保养记录。事后登记方式。
	 * 指定appointment为false。
	 * car.nextCareMile设置为car.mileage+mileInterval
	 * @param carCare
	 */
	public void saveCarCare(CarCare carCare);
	
	/**
	 * 预约保养。事前登记。
	 * 生成CarCare，设置fromDate和toDate作为调度的时间依据，并置appointment为true。其余内容使用updateCarCare设置。
	 * @param car
	 * @param date
	 */
	public void carCareAppointment(Car car,User driver, Date date);
	
	public CarCare getCarCareById(long id);
	
	public PageBean<CarCare> queryCarCare(int pageNum , QueryHelper helper);
	
	/**
	 * 
	 * @param carCare
	 * @return carCare.appointment==true
	 */
	public boolean canDeleteCarCare(CarCare carCare);
	
	public void deleteCarCareById(long id);
	
	/**
	 * 
	 * @param carCare
	 * @return carCare.appointment==true
	 */
	public boolean canUpdateCarCare(CarCare carCare);
	
	/**
	 * 修改保养记录。只能对预约的保养记录进行修改。
	 * 设置appointment为false
	 * 关于设置车辆下次保养里程，请参见saveCarCare的说明。
	 * @param carCare
	 */
	public void updateCarCare(CarCare carCare);
	
	/**
	 * 列出所有需要提醒保养的车辆。条件： nextCareMile-mileage<300
	 * 按nextCareMile-mileage升序排列
	 * @return
	 */
	public PageBean<Car> getNeedCareCars(int pageNum);
	
	public BigDecimal statisticCarCare(Date fromDate,Date toDate);
}
