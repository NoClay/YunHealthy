#ifndef FALLDOWN_H
#define FALLDOWN_H

#include <math.h>
#include <stdlib.h>//据说这里面和math.h都有math.h，为了避免警告就都加上了
#include "mpu6050.h"

#define ARRLENGTH 20
#define ACCELTHRESHOLD 4000//“加加速度”的阈值
#define GYROTHRESHOLD 1500//“角加速度”的阈值

extern unsigned short int AccelArr[ARRLENGTH];
extern unsigned short int GyroArr[ARRLENGTH];

extern unsigned char ArrPoint, FallDownMark;
extern unsigned short int a_old, a_new, g_old, g_new;//a、g的old、new分别用来保存加速度、陀螺仪上一次和这一次的测量数据
extern unsigned short int FallCountOld, FallCountNow;

unsigned char MaybeFallDown(void);
unsigned char FallDown(void);

#endif
