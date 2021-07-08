package com.zhimzhou.bean.converter;

import com.zhimzhou.bean.BO.UserBO;
import com.zhimzhou.bean.DO.UserDO;

public class UserBODOConverter {
  public static UserDO to(UserBO data) {
    if (data == null) {
      return null;
    }
    UserDO instance = new UserDO();
    return instance;
  }

  public static UserBO from(UserDO data) {
    if (data == null) {
      return null;
    }
    return new UserBO();
  }
}
