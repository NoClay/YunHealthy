#ifndef IO_H
#define IO_H
#include "sys.h"

//io�˿ڶ���
#define GPS_EN PAout(4)
#define GSM_EN PAout(5)
#define GSM_RST PAout(7)

void IO_Init(void);//��ʼ��

#endif
