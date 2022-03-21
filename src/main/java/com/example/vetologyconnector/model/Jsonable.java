package com.example.vetologyconnector.model;

import com.example.vetologyconnector.utils.Utils;

public interface Jsonable {
  default String toJson(){
    return Utils.toJson(this);
  }

}
