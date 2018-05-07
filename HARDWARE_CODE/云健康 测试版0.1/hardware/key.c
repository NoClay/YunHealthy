#include "key.h"
#include "delay.h"

//按键状态标识
//0 无按键
//1 短按
//2 长按
unsigned char KeyStatus = 0;

void Key_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//使能PB时钟
	
	//PB13设为上拉，按下时为低电平
	GPIOB->CRH &= 0xFF0FFFFF;
	GPIOB->CRH |= 0x00800000;//PB13上拉输入
	GPIOB->ODR |= 0x00002000;//输出高电平
	
	//配置中断服务
	Ex_NVIC_Config(GPIO_B, 13, FTIR);//下降沿触发
	MY_NVIC_Init(2,2,EXTI15_10_IRQn,2);//抢占2，子优先级2，组2
}

//按键处理函数，普通版
//返回值：
//0，没有按
//1，短按，500ms以下认为是短按
//2，长按
//unsigned char Key_Scan(void)
//{
//	static unsigned short int KeyCount = 0;//

//	if(KEY == 0)
//	{
//		delay_ms(10);//去抖

//		if(KEY == 0)
//		{
//			KeyCount++;
//		}
//	}
//	
//	if(KEY == 1 && KeyCount > 50)//这个函数调用50次就是500ms
//	{
//		KeyCount = 0;
//		return 2;
//	}
//	if(KEY == 1 && KeyCount >= 1)
//	{
//		KeyCount = 0;
//		return 1;
//	}
//	
//	return 0;// 无按键按下
//}


//按键处理函数，中断版
//外部中断10到15的中断服务函数
//按键在PB13
//慢慢修改，不着急（才怪）
void EXTI15_10_IRQHandler(void)
{
	delay_ms(10);//去抖

	if(KEY == 0)
	{
		KeyStatus = 1;
	}
	
	//500ms后如果按键还被按下，则认为是长按
	delay_ms(500);
	
	if(KEY == 0)
	{
		KeyStatus = 2;
	}
	
	EXTI->PR = 0x000002000;//清除中断标志位
}
