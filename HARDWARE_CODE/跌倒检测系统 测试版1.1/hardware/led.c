#include "sys.h"
#include "led.h"

//��ʼ��PD2Ϊ�������ʹ������ڵ�ʱ��
//LED IO��ʼ��
void LED_Init(void)
{
	RCC->APB2ENR |= 0x00000020;//ʹ��PDʱ��
	
	GPIOD->CRL &= 0XFFFFF0FF;
	GPIOD->CRL |= 0X00000300;//PD2�������
	GPIOD->ODR |= 0x00000004;//�����
}
