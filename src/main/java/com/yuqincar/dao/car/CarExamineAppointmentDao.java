package com.yuqincar.dao.car;

import com.yuqincar.dao.common.BaseDao;
import com.yuqincar.domain.car.Car;
import com.yuqincar.domain.car.CarExamineAppointment;

public interface CarExamineAppointmentDao extends BaseDao<CarExamineAppointment> {
	
	public boolean isExistAppointment(long selfId,Car car);
}
