#ifndef USART3_H
#define USART3_H

#include "sys.h"

#define RAS 4//receive array size,���ܴ������ݵ������С���밴ʵ����Ҫ�޸Ĵ�С�������У��λ

#define RECHEAD1 0x5A//�������ݵ�֡ͷ
#define RECHEAD2 0xBB//�������ݵ�֡ͷ

extern unsigned char Com3RecArr[RAS];//���ܴ������ݵ����飬�밴ʵ����Ҫ�޸Ĵ�С

//�⼸λ�ǲ���Ҳѧ���ı�д��ʽ��
//bit ComFlag = 0;//�������ݱ�־λ��Ҳ�ɱ�ʾ�����������շ�
extern unsigned char Com3RecComplete;//���ݽ��ճɹ��ı�־λ��������ɺ����ֶ����㣡����

void USART3_Init(unsigned int pclk1, unsigned int bound);

#endif
