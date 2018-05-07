#include "sys.h"
#include "io.h"

//初始化PD2为输出，并使能这个口的时钟
//LED IO初始化
void IO_Init(void)
{
	RCC->APB2ENR |= 0x00000004;//使能PA时钟
	
	GPIOA->CRL &= 0XFFF0FFFF;
	GPIOA->CRL |= 0X00030000;//PA4推挽输出
	GPIOA->ODR |= 0x00000010;//输出高
	
	GPIOA->CRL &= 0XFF0FFFFF;
	GPIOA->CRL |= 0X00300000;//PA5推挽输出
	GPIOA->ODR |= 0x00000020;//输出高
}
