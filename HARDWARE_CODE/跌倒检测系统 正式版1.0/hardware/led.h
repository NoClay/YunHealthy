#ifndef LED_H
#define LED_H
#include "sys.h"

//LED端口定义
#define LED PDout(2)//PD2

void LED_Init(void);//初始化

#endif
