#ifndef USART2PM25_H
#define USART2PM25_H

#include "sys.h"

//pm2.5����֡ͷ����У��һ��5�ֽڣ�֡βΪ0xFF����������
#define RAS 7//receive array size,���ܴ������ݵ������С���밴ʵ����Ҫ�޸Ĵ�С�������У��λ

//pm2.5�Ĺ̶�֡ͷ��AA����ֻ��һ��
#define RECHEAD1 0xAA//�������ݵ�֡ͷ
//#define RECHEAD2 0xBB//�������ݵ�֡ͷ

extern unsigned char Com2RecArr[RAS];//���ܴ������ݵ����飬�밴ʵ����Ҫ�޸Ĵ�С

//�⼸λ�ǲ���Ҳѧ���ı�д��ʽ��
//bit ComFlag = 0;//�������ݱ�־λ��Ҳ�ɱ�ʾ�����������շ�
extern unsigned char Com2RecComplete;//���ݽ��ճɹ��ı�־λ��������ɺ����ֶ����㣡����

void USART2_Init(unsigned int pclk1, unsigned int bound);

#endif
