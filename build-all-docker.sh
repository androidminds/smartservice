#!/bin/bash

build() {
  cd $1
  gradle buildDocker
  cd ..
}

build EurekaService
build ZuulService
build ConfigService
build ZipkinService
build UserService
build JwtService

