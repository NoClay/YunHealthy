#include "read24c16.h"
#include "STMIIC.h"

//��Ҫ�����ڴ����⣡������
void ReadBG(unsigned char *p)
{
	unsigned short int i;
	
	//�Ȱѻ���������0xFF���
	for (i=0; i<2048; i++)
	{
		p[i] = 0xFF;
	}
	
	IICstart();
	IICwrite(0xa8);//ֱ�Ӵ�ͷ�����������Ժ�����취����
	SlaRes();
	IICwrite(0x00);//ָ����ַ
	SlaRes();
	IICstart();
	IICwrite(0xA1);//���Ͷ��ź�
	SlaRes();

	for (i=0; i<2048; i++)
	{
		p[i] = IICread();
		MasRes();
	}
	
	NoRes();
	IICstop();
}

void DealBG(unsigned char *p, unsigned char *out)
{
	unsigned short int i,j,n,x;

	n = p[2004];//������ݱ������Ѫ�����ݸ���

	//6�ֽ�Ϊһ�飬��ȥ����У��λ��������
	x = 0;
	for (i=0; i<n; i++)
	{
		for(j=0; j<6; j++)
		{
			out[x] = p[i*8+j];
			x++;
		}
	}
	
	//�������0xFF���
	for(i=n*6; i<1320; i++)
	{
		out[i] = 0xFF;
	}
}
