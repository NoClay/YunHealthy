#include "sys.h"
#include "io.h"

//��ʼ��PD2Ϊ�������ʹ������ڵ�ʱ��
//LED IO��ʼ��
void IO_Init(void)
{
	RCC->APB2ENR |= 0x00000004;//ʹ��PAʱ��
	
	GPIOA->CRL &= 0XFFF0FFFF;
	GPIOA->CRL |= 0X00030000;//PA4�������
	GPIOA->ODR |= 0x00000010;//�����
	
	GPIOA->CRL &= 0XFF0FFFFF;
	GPIOA->CRL |= 0X00300000;//PA5�������
	GPIOA->ODR |= 0x00000020;//�����
}
