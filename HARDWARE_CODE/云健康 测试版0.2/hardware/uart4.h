#ifndef UART4_H
#define UART4_H

#include "sys.h"

#define C4RAS 4//com4 receive array size,���ܴ������ݵ������С���밴ʵ����Ҫ�޸Ĵ�С�������У��λ

#define COM4HEAD1 0xAB//�������ݵ�֡ͷ
#define COM4HEAD2 0xCD//�������ݵ�֡ͷ

extern unsigned char Com4RecArr[C4RAS];//���ܴ������ݵ����飬�밴ʵ����Ҫ�޸Ĵ�С

extern unsigned char Com4RecComplete;//���ݽ��ճɹ��ı�־λ��������ɺ����ֶ����㣡����

void UART4_Init(unsigned int pclk1, unsigned int bound);

#endif
