package com.yuqincar.service.car.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuqincar.dao.car.CarWashDao;
import com.yuqincar.dao.car.CarWashShopDao;
import com.yuqincar.domain.car.CarCare;
import com.yuqincar.domain.car.CarWash;
import com.yuqincar.domain.car.CarWashShop;
import com.yuqincar.domain.common.PageBean;
import com.yuqincar.service.car.CarWashService;
import com.yuqincar.utils.QueryHelper;

@Service
public class CarWashServiceImpl implements CarWashService {
	@Autowired
	private CarWashShopDao carWashShopDao;
	@Autowired
	private CarWashDao carWashDao;
	
	@Transactional
	public void saveCarWashShop(CarWashShop carWashShop) {
		carWashShopDao.save(carWashShop);
	}

	public boolean canDeleteCarWashShop(Long id) {
		return carWashShopDao.canDeleteCarWashShop(id);
	}

	@Transactional
	public void deleteCarWashShop(Long id) {
		carWashShopDao.delete(id);
	}

	public CarWashShop getCarWashShopById(Long id) {
		return carWashShopDao.getById(id);
	}

	public List<CarWashShop> getAllCarWashShop() {
		return carWashShopDao.getAll();
	}

	@Transactional
	public void saveCarWash(CarWash carWash) {
		carWashDao.save(carWash);
	}

	@Transactional
	public void updateCarWash(CarWash carWash) {
		carWashDao.update(carWash);
	}

	@Transactional
	public void deleteCarWash(Long id) {
		carWashDao.delete(id);
	}

	public PageBean<CarWash> queryCarWash(int pageNum, QueryHelper helper) {
		return carWashDao.getPageBean(pageNum, helper);
	}

	public CarWash getCarWashById(Long id) {
		return carWashDao.getById(id);
	}
	@Transactional
	public void importExcelFile(List<CarWash> carWashs){
		
		for(int i=0;i<carWashs.size();i++){
			carWashDao.save(carWashs.get(i));
		}	
	}
}
