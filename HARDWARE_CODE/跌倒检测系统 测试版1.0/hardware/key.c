#include "key.h"
#include "delay.h"

void Key_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//使能PB时钟
	
	//PB13设为上拉，按下时为低电平
	GPIOB->CRH &= 0xFF0FFFFF;
	GPIOB->CRH |= 0x00800000;//PB13上拉输入
	GPIOB->ODR |= 0x00002000;//输出高电平
}

//按键处理函数
//返回值：
//0，没有按
//1，短按，500ms以下认为是短按
//2，长按
unsigned char Key_Scan(void)
{

	static unsigned short int KeyCount = 0;//

	if(KEY == 0)
	{
		delay_ms(10);//去抖

		if(KEY == 0)
		{
			KeyCount++;
		}
	}
	
	if(KEY == 1 && KeyCount > 50)//这个函数调用50次就是500ms
	{
		KeyCount = 0;
		return 2;
	}
	if(KEY == 1 && KeyCount >= 1)
	{
		KeyCount = 0;
		return 1;
	}
	
	return 0;// 无按键按下

	
//	if(KEY == 0)
//	{
//		delay_ms(10);//去抖

//		if(KEY == 0)
//		{
//			return 1;
//		}
//	}
//	return 0;
}
