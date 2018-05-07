#include "falldown.h"
/*
#include <math.h>
#include <stdlib.h>//据说这里面和math.h都有math.h，为了避免警告就都加上了
#include "mpu6050.h"

#define ARRLENGTH 20
#define ACCELTHRESHOLD 4000//“加加速度”的阈值
#define GYROTHRESHOLD 1500//“角加速度”的阈值

unsigned char falldown(void);
*/

unsigned short int AccelArr[ARRLENGTH] = {0};
unsigned short int GyroArr[ARRLENGTH] = {0};

unsigned char ArrPoint = 0, FallDownMark = 0;
unsigned short int a_old = 0, a_new = 0 , g_old = 0, g_new = 0;//a、g的old、new分别用来保存加速度、陀螺仪上一次和这一次的测量数据
unsigned short int FallCountOld = 0, FallCountNow = 0;

//short int agd[6] = {0};

//目前仅考虑加速度
//加速度用“加速度的微分”
//角速度只需要“平方和求绝对值”
//加速度用“加速度的微分”，“加加速度”的阈值大约在4000(1g/s^3)
//角速度只需要“平方和求绝对值”，阈值4000(约120度/s)
//如果用“角加速度”，阈值应该在1500(约20度/s^2)
//加加速度20个有5个(或6个)，角速度20个有10个，角加速度20个有3个。
unsigned char MaybeFallDown(void)
{
	unsigned char i;
	unsigned char AccelThresholdNum = 0, GyroThresholdNum = 0;
	unsigned short int a_dt, g_dt;

	//一个计数器
	FallCountNow++;
	
	//保存上一次的数据
	a_old = a_new;
	g_old = g_new;
	//计算新值
	a_new = (unsigned short int)sqrt(pow(agd[0],2) + pow(agd[1],2) + pow(agd[2],2));
	g_new = (unsigned short int)sqrt(pow(agd[3],2) + pow(agd[4],2) + pow(agd[5],2));
	//求微分
	a_dt = (unsigned short int)abs(a_new - a_old);
	g_dt = (unsigned short int)abs(g_new - g_old);

	//将加速度、陀螺仪处理后的数据存入长度为ARRLENGTH的数组中（准确地说是队列）
	if(ArrPoint == ARRLENGTH)
	{
		ArrPoint = 0;
	}
	AccelArr[ArrPoint] = a_dt;
	GyroArr[ArrPoint] = g_dt;
	ArrPoint++;
	
	//扫描“加加速度”数组中超过阈值的数据的个数
	AccelThresholdNum = 0;
	for(i = 0; i<ARRLENGTH; i++)
	{
		if(AccelArr[i] > ACCELTHRESHOLD)
		{
			AccelThresholdNum++;
		}
	}

	//扫描陀螺仪数组中超过阈值的数据的个数
	GyroThresholdNum = 0;
	for(i = 0; i<ARRLENGTH; i++)
	{
		if(GyroArr[i] > GYROTHRESHOLD)
		{
			GyroThresholdNum++;
		}
	}
	
	//如果加速度数组中有5个超过阈值，陀螺仪数组中有3个超过阈值，则可以认为摔倒。
	if(AccelThresholdNum > 5 && GyroThresholdNum > 3)
	{
		//printf("Fall Down!\n");
		//判定摔倒后将数组清零
		for(i=0; i<20; i++)
		{
			AccelArr[i] = 0;
			GyroArr[i] = 0;
		}
		FallDownMark = 1;
		return 1;//摔倒返回1
	}

	return 0;//没摔倒返回0
}

unsigned char FallDown(void)
{
	//如果可能摔倒了，标记一下
	if(MaybeFallDown())
	{
		FallDownMark++;
		FallCountOld = FallCountNow;
	}

	//可能摔倒后4秒内没动静就报警，这样一定程度上可以避免持续剧烈运动导致的误报警
	//但是，持续剧烈运动后停止则会报警
	//总之这个算法很不完善，有个样子就行，写出专业算法不是我们能做的。
	if(FallDownMark && (abs(FallCountNow - FallCountOld) > 400))
	{
		FallCountOld = 0;
		FallCountNow = 0;
		FallDownMark = 0;
		
		return 1;
	}
	
	return 0;
}
