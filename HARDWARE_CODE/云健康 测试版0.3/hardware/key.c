#include "key.h"
#include "delay.h"


void Key_Init(void)
{
	RCC->APB2ENR |= 1 << 2;
	RCC->APB2ENR |= 1 << 4;
	
	JTAG_Set(SWD_ENABLE);//在这个开发板上需要这样设置，如果自己做板子的话我不知道
	
	//PA0，高电平有效，设置下拉
	GPIOA->CRL &= 0xFFFFFFF0;
	GPIOA->CRL |= 0x00000008;
	GPIOA->ODR &= 0xFFFFFFFE;
	
	//PA15，这两个都是低电平有效，设置上拉
	GPIOA->CRH &= 0x0FFFFFFF;
	GPIOA->CRH |= 0x80000000;
	GPIOA->ODR |= 1 << 15;
	
	//PC5
	GPIOC->CRL &= 0xFF0FFFFF;
	GPIOC->CRL |= 0x00800000;
	GPIOC->ODR |= 1 << 5;
}

//按键处理函数
//返回按键值
//mode:0,不支持连续按;1,支持连续按;
//
//返回值：
//0，没有任何按键按下
//KEY0_PRES，KEY0按下
//KEY1_PRES，KEY1按下
//WKUP_PRES，WK_UP按下 
//注意此函数有响应优先级,KEY0>KEY1>WK_UP!!
u8 Key_Scan(u8 mode)
{	 
	static u8 key_up = 1;//按键按松开标志，1为松开，0为按下，同时为实现连按做铺垫
	if(mode)
	{
		key_up=1;//支持连按
	}
	if(key_up && (KEY0==0 || KEY1==0 || WK_UP==1))
	{
		delay_ms(10);//去抖
		key_up=0;
		if(KEY0==0)
		{
			return KEY0_PRES;//相当于return 1;
		}
		else if(KEY1==0)
		{
			return KEY1_PRES;//相当于return 2;
		}
		else if(WK_UP==1)
		{
			return WKUP_PRES;//相当于return 3;
		}
	}
	else if(KEY0==1 && KEY1==1 && WK_UP==0)
	{
		key_up=1;
	}
	return 0;// 无按键按下
}
