package com.jbs.pm.adapter.input.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

  @GetMapping("/siemka")
  public String siemka() {
    return "siemka";
  }
}
