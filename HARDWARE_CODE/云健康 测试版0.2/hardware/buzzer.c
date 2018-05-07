#include "buzzer.h"
#include "delay.h"

void Buzzer_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//ʹ��PBʱ��
	
	GPIOB->CRH &= 0x0FFFFFFF;
	GPIOB->CRH |= 0x30000000;//PB15�������
	GPIOB->ODR |= 0x00008000;//����ߵ�ƽ
}

//����������ʱ�䣬���ʱ�䣬ѭ������
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
