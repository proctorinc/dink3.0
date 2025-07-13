package com.dink3.institutions.dto;

import com.dink3.accounts.dto.AccountDto;
import com.dink3.plaid.dto.PlaidItemDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstitutionFullDto {

    @JsonUnwrapped
    private InstitutionDto institution;

    @JsonProperty("syncItem")
    private PlaidItemDto plaidItem;

    private List<AccountDto> accounts;
}
