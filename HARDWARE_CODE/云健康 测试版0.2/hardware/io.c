#include "sys.h"
#include "io.h"

//初始化PD2为输出，并使能这个口的时钟
//LED IO初始化
void IO_Init(void)
{
	RCC->APB2ENR |= 0x00000004;//使能PA时钟
	
	GPIOA->CRL &= 0xFFF0FFFF;
	GPIOA->CRL |= 0x00030000;//PA4推挽输出
	GPIOA->ODR |= 0x00000010;//输出高
	
	GPIOA->CRL &= 0xFF0FFFFF;
	GPIOA->CRL |= 0x00300000;//PA5推挽输出
	GPIOA->ODR |= 0x00000020;//输出高
	
	GPIOA->CRL &= 0x0FFFFFFF;
	GPIOA->CRL |= 0x30000000;//PA5推挽输出
	GPIOA->ODR |= 0x00000080;//输出高
}
