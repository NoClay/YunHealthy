#include "sys.h"
#include "led.h"

//初始化PD2为输出，并使能这个口的时钟
//LED IO初始化
void LED_Init(void)
{
	RCC->APB2ENR |= 0x00000020;//使能PD时钟
	
	GPIOD->CRL &= 0XFFFFF0FF;
	GPIOD->CRL |= 0X00000300;//PD2推挽输出
	GPIOD->ODR |= 0x00000004;//输出高
}
