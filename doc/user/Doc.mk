all : TEXTS


######################################################################

TEXTS : select-securities.pdf


######################################################################
# TEXTS

select-securities.pdf : select-securities.md
	pandoc -f markdown -t pdf -V geometry:a4paper -i $< -o $@


######################################################################
# Various

%.pdf : %.odt
	ooo2pdf.sh $< $@

%.pdf : %.odg
	ooo2pdf.sh $< $@

%.png : %.pdf
	pdf2png_p1.sh -i $<
