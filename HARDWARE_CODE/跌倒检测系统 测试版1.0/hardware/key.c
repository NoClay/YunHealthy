#include "key.h"
#include "delay.h"

void Key_Init(void)
{
	RCC->APB2ENR |= 0x00000008;//ʹ��PBʱ��
	
	//PB13��Ϊ����������ʱΪ�͵�ƽ
	GPIOB->CRH &= 0xFF0FFFFF;
	GPIOB->CRH |= 0x00800000;//PB13��������
	GPIOB->ODR |= 0x00002000;//����ߵ�ƽ
}

//����������
//����ֵ��
//0��û�а�
//1���̰���500ms������Ϊ�Ƕ̰�
//2������
unsigned char Key_Scan(void)
{

	static unsigned short int KeyCount = 0;//

	if(KEY == 0)
	{
		delay_ms(10);//ȥ��

		if(KEY == 0)
		{
			KeyCount++;
		}
	}
	
	if(KEY == 1 && KeyCount > 50)//�����������50�ξ���500ms
	{
		KeyCount = 0;
		return 2;
	}
	if(KEY == 1 && KeyCount >= 1)
	{
		KeyCount = 0;
		return 1;
	}
	
	return 0;// �ް�������

	
//	if(KEY == 0)
//	{
//		delay_ms(10);//ȥ��

//		if(KEY == 0)
//		{
//			return 1;
//		}
//	}
//	return 0;
}
