#include "buzzer.h"

void Buzzer_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//ʹ��PBʱ��
	
	GPIOB->CRH &= 0x0FFFFFFF;
	GPIOB->CRH |= 0x30000000;//PB15�������
	GPIOB->ODR |= 0x00008000;//����ߵ�ƽ
}
