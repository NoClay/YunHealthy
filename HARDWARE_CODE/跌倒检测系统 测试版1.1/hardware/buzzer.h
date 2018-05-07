#ifndef BUZZER_H
#define BUZZER_H	 
#include "sys.h"

//蜂鸣器接口定义
#define BUZZER PBout(15)

void Buzzer_Init(void);	//初始化
void BuzzerWork(unsigned short int work, unsigned short int pause, unsigned char times);//蜂鸣器工作时间，间隔时间，循环次数

#endif
