#include "falldown.h"
/*
#include <math.h>
#include <stdlib.h>//��˵�������math.h����math.h��Ϊ�˱��⾯��Ͷ�������
#include "mpu6050.h"

#define ARRLENGTH 20
#define ACCELTHRESHOLD 4000//���Ӽ��ٶȡ�����ֵ
#define GYROTHRESHOLD 1500//���Ǽ��ٶȡ�����ֵ

unsigned char falldown(void);
*/

unsigned short int AccelArr[ARRLENGTH] = {0};
unsigned short int GyroArr[ARRLENGTH] = {0};

unsigned char ArrPoint = 0, FallDownMark = 0;
unsigned short int a_old = 0, a_new = 0 , g_old = 0, g_new = 0;//a��g��old��new�ֱ�����������ٶȡ���������һ�κ���һ�εĲ�������
unsigned short int FallCountOld = 0, FallCountNow = 0;

//short int agd[6] = {0};

//Ŀǰ�����Ǽ��ٶ�
//���ٶ��á����ٶȵ�΢�֡�
//���ٶ�ֻ��Ҫ��ƽ���������ֵ��
//���ٶ��á����ٶȵ�΢�֡������Ӽ��ٶȡ�����ֵ��Լ��4000(1g/s^3)
//���ٶ�ֻ��Ҫ��ƽ���������ֵ������ֵ4000(Լ120��/s)
//����á��Ǽ��ٶȡ�����ֵӦ����1500(Լ20��/s^2)
//�Ӽ��ٶ�20����5��(��6��)�����ٶ�20����10�����Ǽ��ٶ�20����3����
unsigned char MaybeFallDown(void)
{
	unsigned char i;
	unsigned char AccelThresholdNum = 0, GyroThresholdNum = 0;
	unsigned short int a_dt, g_dt;

	//һ��������
	FallCountNow++;
	
	//������һ�ε�����
	a_old = a_new;
	g_old = g_new;
	//������ֵ
	a_new = (unsigned short int)sqrt(pow(agd[0],2) + pow(agd[1],2) + pow(agd[2],2));
	g_new = (unsigned short int)sqrt(pow(agd[3],2) + pow(agd[4],2) + pow(agd[5],2));
	//��΢��
	a_dt = (unsigned short int)abs(a_new - a_old);
	g_dt = (unsigned short int)abs(g_new - g_old);

	//�����ٶȡ������Ǵ��������ݴ��볤��ΪARRLENGTH�������У�׼ȷ��˵�Ƕ��У�
	if(ArrPoint == ARRLENGTH)
	{
		ArrPoint = 0;
	}
	AccelArr[ArrPoint] = a_dt;
	GyroArr[ArrPoint] = g_dt;
	ArrPoint++;
	
	//ɨ�衰�Ӽ��ٶȡ������г�����ֵ�����ݵĸ���
	AccelThresholdNum = 0;
	for(i = 0; i<ARRLENGTH; i++)
	{
		if(AccelArr[i] > ACCELTHRESHOLD)
		{
			AccelThresholdNum++;
		}
	}

	//ɨ�������������г�����ֵ�����ݵĸ���
	GyroThresholdNum = 0;
	for(i = 0; i<ARRLENGTH; i++)
	{
		if(GyroArr[i] > GYROTHRESHOLD)
		{
			GyroThresholdNum++;
		}
	}
	
	//������ٶ���������5��������ֵ����������������3��������ֵ���������Ϊˤ����
	if(AccelThresholdNum > 5 && GyroThresholdNum > 3)
	{
		//printf("Fall Down!\n");
		//�ж�ˤ������������
		for(i=0; i<20; i++)
		{
			AccelArr[i] = 0;
			GyroArr[i] = 0;
		}
		FallDownMark = 1;
		return 1;//ˤ������1
	}

	return 0;//ûˤ������0
}

unsigned char FallDown(void)
{
	//�������ˤ���ˣ����һ��
	if(MaybeFallDown())
	{
		FallDownMark++;
		FallCountOld = FallCountNow;
	}

	//����ˤ����4����û�����ͱ���������һ���̶��Ͽ��Ա�����������˶����µ��󱨾�
	//���ǣ����������˶���ֹͣ��ᱨ��
	//��֮����㷨�ܲ����ƣ��и����Ӿ��У�д��רҵ�㷨�������������ġ�
	if(FallDownMark && (abs(FallCountNow - FallCountOld) > 400))
	{
		FallCountOld = 0;
		FallCountNow = 0;
		FallDownMark = 0;
		
		return 1;
	}
	
	return 0;
}
