#ifndef BUZZER_H
#define BUZZER_H	 
#include "sys.h"

//�������ӿڶ���
#define BUZZER PBout(15)

void Buzzer_Init(void);	//��ʼ��
void BuzzerWork(unsigned short int work, unsigned short int pause, unsigned char times);//����������ʱ�䣬���ʱ�䣬ѭ������

#endif
