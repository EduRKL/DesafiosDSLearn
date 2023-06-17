package com.desafio.crud.services;

import com.desafio.crud.dto.ClientDto;
import com.desafio.crud.entities.Client;
import com.desafio.crud.repositories.ClientRepository;
import com.desafio.crud.services.exceptions.DatabaseException;
import com.desafio.crud.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@Service
public class ClientService implements Serializable {
    private static final long serialVersionUID = 1L;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<ClientDto> findAllPaged(PageRequest pageRequest){
        Page<Client> clientList = clientRepository.findAll(pageRequest);

    return clientList.map(ClientDto::new);


    }

    @Transactional(readOnly = true)
    public ClientDto findById(Long id){
        Optional<Client> obj = clientRepository.findById(id);
        Client entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not Found"));
        return new ClientDto(entity);
    }

    @Transactional
    public ClientDto insert(ClientDto dto) {
        Client entity = new Client();
        copyClientDtoToEntity(dto, entity);
        entity = clientRepository.save(entity);
        return new ClientDto(entity);

    }



    @Transactional
    public ClientDto update(Long id, ClientDto dto) {
        try {

        Client entity = clientRepository.getReferenceById(id);
        copyClientDtoToEntity(dto, entity);
        entity = clientRepository.save(entity);
        return new ClientDto(entity);
        }
        catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try{
            clientRepository.deleteById(id);
        }
        catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Id not found " + id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyClientDtoToEntity(ClientDto dto, Client entity){
        entity.setName(dto.getName());
        entity.setCpf(dto.getCpf());
        entity.setBirthDate(dto.getBirthDate());
        entity.setIncome(dto.getIncome());
        entity.setChildren(dto.getChildren());
    }
}
