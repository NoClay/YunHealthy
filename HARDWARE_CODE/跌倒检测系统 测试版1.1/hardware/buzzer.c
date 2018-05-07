#include "buzzer.h"
#include "delay.h"

void Buzzer_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//使能PB时钟
	
	GPIOB->CRH &= 0x0FFFFFFF;
	GPIOB->CRH |= 0x30000000;//PB15推挽输出
	GPIOB->ODR |= 0x00008000;//输出高电平
}

//蜂鸣器工作时间，间隔时间，循环次数
void BuzzerWork(unsigned short int work, unsigned short int pause, unsigned char times)
{
	unsigned char i;
	for(i=0; i<times; i++)
	{
		BUZZER = 0;
		delay_ms(work);
		BUZZER = 1;
		delay_ms(pause);
	}
	
	//BUZZER = 1;
}
