#ifndef FALLDOWN_H
#define FALLDOWN_H

#include <math.h>
#include <stdlib.h>//��˵�������math.h����math.h��Ϊ�˱��⾯��Ͷ�������
#include "mpu6050.h"

#define ARRLENGTH 20
#define ACCELTHRESHOLD 4000//���Ӽ��ٶȡ�����ֵ
#define GYROTHRESHOLD 1500//���Ǽ��ٶȡ�����ֵ

extern unsigned short int AccelArr[ARRLENGTH];
extern unsigned short int GyroArr[ARRLENGTH];

extern unsigned char ArrPoint, FallDownMark;
extern unsigned short int a_old, a_new, g_old, g_new;//a��g��old��new�ֱ�����������ٶȡ���������һ�κ���һ�εĲ�������
extern unsigned short int FallCountOld, FallCountNow;

unsigned char MaybeFallDown(void);
unsigned char FallDown(void);

#endif
