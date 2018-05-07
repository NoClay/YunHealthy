#include "buzzer.h"

void Buzzer_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//使能PB时钟
	
	GPIOB->CRH &= 0x0FFFFFFF;
	GPIOB->CRH |= 0x30000000;//PB15推挽输出
	GPIOB->ODR |= 0x00008000;//输出高电平
}
